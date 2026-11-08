package com.mahesh.daw.service;

import com.mahesh.daw.dto.RequestResponse;
import com.mahesh.daw.entity.*;
import com.mahesh.daw.exception.ForbiddenOperationException;
import com.mahesh.daw.exception.WorkflowConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApprovalWorkflowService {

    private final RequestService requestService;
    private final UserService userService;
    private final ApprovalHistoryService approvalHistoryService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @Transactional
    public RequestResponse startManagerReview(Long requestId, String actorEmail) {
        User manager = getActiveActor(actorEmail, "MANAGER");
        Request request = requestService.getRequestById(requestId);

        validateStatus(request, RequestStatus.SUBMITTED);
        request.setStatus(RequestStatus.MANAGER_REVIEW);

        Request saved = requestService.updateRequest(request);
        createAuditLog(saved, manager, "MANAGER_REVIEW_STARTED",
                "Manager review started");

        return RequestResponse.from(saved);
    }

    @Transactional
    public RequestResponse approveManagerRequest(
            Long requestId, String actorEmail, String comments) {

        User manager = getActiveActor(actorEmail, "MANAGER");
        Request request = requestService.getRequestById(requestId);

        validateStatus(request, RequestStatus.MANAGER_REVIEW);

        RequestStatus previous = request.getStatus();
        request.setStatus(RequestStatus.MANAGER_APPROVED);

        Request saved = requestService.updateRequest(request);

        createApprovalHistory(saved, manager, ApprovalAction.APPROVED,
                comments, previous, RequestStatus.MANAGER_APPROVED);

        createAuditLog(saved, manager, "MANAGER_APPROVED",
                "Request approved by manager");

        createNotification(saved.getRequester(), saved,
                "Request Manager Approved",
                "Your request has been approved by the manager.");

        return RequestResponse.from(saved);
    }

    @Transactional
    public RequestResponse rejectManagerRequest(
            Long requestId, String actorEmail, String comments) {

        return rejectAtStage(requestId, actorEmail, comments,
                "MANAGER", RequestStatus.MANAGER_REVIEW);
    }

    @Transactional
    public RequestResponse returnManagerRequest(
            Long requestId, String actorEmail, String comments) {

        return returnAtStage(requestId, actorEmail, comments,
                "MANAGER", RequestStatus.MANAGER_REVIEW);
    }

    @Transactional
    public RequestResponse startAdminReview(Long requestId, String actorEmail) {
        User admin = getActiveActor(actorEmail, "ADMIN");
        Request request = requestService.getRequestById(requestId);

        validateStatus(request, RequestStatus.MANAGER_APPROVED);
        request.setStatus(RequestStatus.ADMIN_REVIEW);

        Request saved = requestService.updateRequest(request);
        createAuditLog(saved, admin, "ADMIN_REVIEW_STARTED",
                "Admin review started");

        return RequestResponse.from(saved);
    }

    @Transactional
    public RequestResponse approveAdminRequest(
            Long requestId, String actorEmail, String comments) {

        User admin = getActiveActor(actorEmail, "ADMIN");
        Request request = requestService.getRequestById(requestId);

        validateStatus(request, RequestStatus.ADMIN_REVIEW);

        RequestStatus previous = request.getStatus();
        request.setStatus(RequestStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());

        Request saved = requestService.updateRequest(request);

        createApprovalHistory(saved, admin, ApprovalAction.APPROVED,
                comments, previous, RequestStatus.COMPLETED);

        createAuditLog(saved, admin, "ADMIN_APPROVED",
                "Request approved by admin");

        createAuditLog(saved, admin, "REQUEST_COMPLETED",
                "Request workflow completed");

        createNotification(saved.getRequester(), saved,
                "Request Approved",
                "Your request has been fully approved.");

        return RequestResponse.from(saved);
    }

    @Transactional
    public RequestResponse rejectAdminRequest(
            Long requestId, String actorEmail, String comments) {

        return rejectAtStage(requestId, actorEmail, comments,
                "ADMIN", RequestStatus.ADMIN_REVIEW);
    }

    @Transactional
    public RequestResponse returnAdminRequest(
            Long requestId, String actorEmail, String comments) {

        return returnAtStage(requestId, actorEmail, comments,
                "ADMIN", RequestStatus.ADMIN_REVIEW);
    }

    private RequestResponse rejectAtStage(
            Long requestId,
            String actorEmail,
            String comments,
            String expectedRole,
            RequestStatus expectedStatus) {

        User approver = getActiveActor(actorEmail, expectedRole);
        Request request = requestService.getRequestById(requestId);

        validateStatus(request, expectedStatus);

        RequestStatus previous = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);

        Request saved = requestService.updateRequest(request);

        createApprovalHistory(saved, approver, ApprovalAction.REJECTED,
                comments, previous, RequestStatus.REJECTED);

        createAuditLog(saved, approver, "REQUEST_REJECTED",
                "Request rejected during " + expectedRole.toLowerCase() + " review");

        createNotification(saved.getRequester(), saved,
                "Request Rejected",
                "Your request has been rejected.");

        return RequestResponse.from(saved);
    }

    private RequestResponse returnAtStage(
            Long requestId,
            String actorEmail,
            String comments,
            String expectedRole,
            RequestStatus expectedStatus) {

        User approver = getActiveActor(actorEmail, expectedRole);
        Request request = requestService.getRequestById(requestId);

        validateStatus(request, expectedStatus);

        RequestStatus previous = request.getStatus();
        request.setStatus(RequestStatus.RETURNED);

        Request saved = requestService.updateRequest(request);

        createApprovalHistory(saved, approver, ApprovalAction.RETURNED,
                comments, previous, RequestStatus.RETURNED);

        createAuditLog(saved, approver, "REQUEST_RETURNED",
                "Request returned for correction");

        createNotification(saved.getRequester(), saved,
                "Request Returned",
                "Your request has been returned for correction.");

        return RequestResponse.from(saved);
    }

    private User getActiveActor(String email, String expectedRole) {
        User actor = userService.getUserByEmail(email);

        if (!Boolean.TRUE.equals(actor.getActive())) {
            throw new ForbiddenOperationException(
                    "Inactive users cannot perform workflow actions");
        }

        if (actor.getRole() == null
                || !expectedRole.equalsIgnoreCase(actor.getRole().getName())) {
            throw new ForbiddenOperationException(
                    "Only an active " + expectedRole.toLowerCase()
                            + " can perform this action");
        }

        return actor;
    }

    private void validateStatus(Request request, RequestStatus expectedStatus) {
        if (request.getStatus() != expectedStatus) {
            throw new WorkflowConflictException(
                    "Request must be in " + expectedStatus
                            + " status. Current status: " + request.getStatus());
        }
    }

    private void createApprovalHistory(
            Request request,
            User approver,
            ApprovalAction action,
            String comments,
            RequestStatus previousStatus,
            RequestStatus newStatus) {

        ApprovalHistory history = ApprovalHistory.builder()
                .request(request)
                .approver(approver)
                .action(action)
                .comments(comments)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .build();

        approvalHistoryService.createHistory(history);
    }

    private void createAuditLog(
            Request request,
            User user,
            String action,
            String description) {

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType("REQUEST")
                .entityId(request.getId())
                .description(description)
                .build();

        auditLogService.createAuditLog(auditLog);
    }

    private void createNotification(
            User user,
            Request request,
            String title,
            String message) {

        if (user == null) {
            return;
        }

        Notification notification = Notification.builder()
                .user(user)
                .request(request)
                .title(title)
                .message(message)
                .readStatus(false)
                .build();

        notificationService.createNotification(notification);
    }
}
