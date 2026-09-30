package com.mahesh.daw.service;

import com.mahesh.daw.entity.ApprovalAction;
import com.mahesh.daw.entity.ApprovalHistory;
import com.mahesh.daw.entity.AuditLog;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.RequestStatus;
import com.mahesh.daw.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApprovalWorkflowService {

    private final RequestService requestService;
    private final UserService userService;
    private final ApprovalHistoryService approvalHistoryService;
    private final AuditLogService auditLogService;

    @Transactional
    public Request submitRequest(Long requestId) {

        Request request = requestService.getRequestById(requestId);

        validateStatus(request, RequestStatus.DRAFT);

        request.setStatus(RequestStatus.SUBMITTED);

        Request savedRequest = requestService.updateRequest(request);

        createAuditLog(
                savedRequest,
                null,
                "REQUEST_SUBMITTED",
                "Request submitted for approval"
        );

        return savedRequest;
    }

    @Transactional
    public Request startManagerReview(Long requestId) {

        Request request = requestService.getRequestById(requestId);

        validateStatus(request, RequestStatus.SUBMITTED);

        request.setStatus(RequestStatus.MANAGER_REVIEW);

        Request savedRequest = requestService.updateRequest(request);

        createAuditLog(
                savedRequest,
                null,
                "MANAGER_REVIEW_STARTED",
                "Manager review started"
        );

        return savedRequest;
    }

    @Transactional
    public Request approveManagerRequest(
            Long requestId,
            Long approverId,
            String comments) {

        Request request = requestService.getRequestById(requestId);

        validateStatus(request, RequestStatus.MANAGER_REVIEW);

        User approver = userService.getUserById(approverId);

        RequestStatus previousStatus = request.getStatus();

        request.setStatus(RequestStatus.MANAGER_APPROVED);

        Request savedRequest = requestService.updateRequest(request);

        createApprovalHistory(
                savedRequest,
                approver,
                ApprovalAction.APPROVED,
                comments,
                previousStatus,
                RequestStatus.MANAGER_APPROVED
        );

        createAuditLog(
                savedRequest,
                approver,
                "MANAGER_APPROVED",
                "Request approved by manager"
        );

        return savedRequest;
    }

    @Transactional
    public Request startAdminReview(Long requestId) {

        Request request = requestService.getRequestById(requestId);

        validateStatus(request, RequestStatus.MANAGER_APPROVED);

        request.setStatus(RequestStatus.ADMIN_REVIEW);

        Request savedRequest = requestService.updateRequest(request);

        createAuditLog(
                savedRequest,
                null,
                "ADMIN_REVIEW_STARTED",
                "Admin review started"
        );

        return savedRequest;
    }

    @Transactional
    public Request approveAdminRequest(
            Long requestId,
            Long approverId,
            String comments) {

        Request request = requestService.getRequestById(requestId);

        validateStatus(request, RequestStatus.ADMIN_REVIEW);

        User approver = userService.getUserById(approverId);

        RequestStatus previousStatus = request.getStatus();

        request.setStatus(RequestStatus.APPROVED);

        Request savedRequest = requestService.updateRequest(request);

        createApprovalHistory(
                savedRequest,
                approver,
                ApprovalAction.APPROVED,
                comments,
                previousStatus,
                RequestStatus.APPROVED
        );

        createAuditLog(
                savedRequest,
                approver,
                "ADMIN_APPROVED",
                "Request approved by admin"
        );

        savedRequest.setStatus(RequestStatus.COMPLETED);

        savedRequest = requestService.updateRequest(savedRequest);

        createAuditLog(
                savedRequest,
                approver,
                "REQUEST_COMPLETED",
                "Request workflow completed"
        );

        return savedRequest;
    }

    @Transactional
    public Request rejectRequest(
            Long requestId,
            Long approverId,
            String comments) {

        Request request = requestService.getRequestById(requestId);

        if (request.getStatus() != RequestStatus.MANAGER_REVIEW
                && request.getStatus() != RequestStatus.ADMIN_REVIEW) {

            throw new IllegalStateException(
                    "Request cannot be rejected from status: "
                            + request.getStatus()
            );
        }

        User approver = userService.getUserById(approverId);

        RequestStatus previousStatus = request.getStatus();

        request.setStatus(RequestStatus.REJECTED);

        Request savedRequest = requestService.updateRequest(request);

        createApprovalHistory(
                savedRequest,
                approver,
                ApprovalAction.REJECTED,
                comments,
                previousStatus,
                RequestStatus.REJECTED
        );

        createAuditLog(
                savedRequest,
                approver,
                "REQUEST_REJECTED",
                "Request rejected during approval"
        );

        return savedRequest;
    }

    @Transactional
    public Request returnRequest(
            Long requestId,
            Long approverId,
            String comments) {

        Request request = requestService.getRequestById(requestId);

        if (request.getStatus() != RequestStatus.MANAGER_REVIEW
                && request.getStatus() != RequestStatus.ADMIN_REVIEW) {

            throw new IllegalStateException(
                    "Request cannot be returned from status: "
                            + request.getStatus()
            );
        }

        User approver = userService.getUserById(approverId);

        RequestStatus previousStatus = request.getStatus();

        request.setStatus(RequestStatus.RETURNED);

        Request savedRequest = requestService.updateRequest(request);

        createApprovalHistory(
                savedRequest,
                approver,
                ApprovalAction.RETURNED,
                comments,
                previousStatus,
                RequestStatus.RETURNED
        );

        createAuditLog(
                savedRequest,
                approver,
                "REQUEST_RETURNED",
                "Request returned for correction"
        );

        return savedRequest;
    }

    private void validateStatus(
            Request request,
            RequestStatus expectedStatus) {

        if (request.getStatus() != expectedStatus) {

            throw new IllegalStateException(
                    "Request must be in "
                            + expectedStatus
                            + " status. Current status: "
                            + request.getStatus()
            );
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
}
