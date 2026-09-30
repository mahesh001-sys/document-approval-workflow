package com.mahesh.daw.service;

import com.mahesh.daw.entity.ApprovalAction;
import com.mahesh.daw.entity.ApprovalHistory;
import com.mahesh.daw.entity.ApprovalStatus;
import com.mahesh.daw.entity.ApprovalStep;
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
    private final ApprovalStepService approvalStepService;
    private final ApprovalHistoryService approvalHistoryService;

    @Transactional
    public Request submitRequest(Long requestId) {

        Request request = requestService.getRequestById(requestId);

        validateStatus(
                request,
                RequestStatus.DRAFT
        );

        RequestStatus previousStatus = request.getStatus();

        request.setStatus(RequestStatus.SUBMITTED);

        Request savedRequest = requestService.updateRequest(request);

        createHistory(
                savedRequest,
                null,
                ApprovalAction.APPROVED,
                previousStatus,
                RequestStatus.SUBMITTED,
                "Request submitted"
        );

        return savedRequest;
    }

    @Transactional
    public Request approveRequest(
            Long requestId,
            Long approverId,
            String comments) {

        Request request = requestService.getRequestById(requestId);

        User approver = new User();
        approver.setId(approverId);

        RequestStatus previousStatus = request.getStatus();

        if (previousStatus == RequestStatus.MANAGER_REVIEW) {

            request.setStatus(RequestStatus.ADMIN_REVIEW);

        } else if (previousStatus == RequestStatus.ADMIN_REVIEW) {

            request.setStatus(RequestStatus.APPROVED);

        } else {

            throw new IllegalStateException(
                    "Request cannot be approved from status: "
                            + previousStatus
            );
        }

        Request savedRequest = requestService.updateRequest(request);

        ApprovalHistory history = ApprovalHistory.builder()
                .request(savedRequest)
                .approver(approver)
                .action(ApprovalAction.APPROVED)
                .comments(comments)
                .previousStatus(previousStatus)
                .newStatus(savedRequest.getStatus())
                .build();

        approvalHistoryService.createHistory(history);

        return savedRequest;
    }

    @Transactional
    public Request rejectRequest(
            Long requestId,
            Long approverId,
            String comments) {

        Request request = requestService.getRequestById(requestId);

        RequestStatus previousStatus = request.getStatus();

        if (previousStatus != RequestStatus.MANAGER_REVIEW
                && previousStatus != RequestStatus.ADMIN_REVIEW) {

            throw new IllegalStateException(
                    "Request cannot be rejected from status: "
                            + previousStatus
            );
        }

        User approver = new User();
        approver.setId(approverId);

        request.setStatus(RequestStatus.REJECTED);

        Request savedRequest = requestService.updateRequest(request);

        ApprovalHistory history = ApprovalHistory.builder()
                .request(savedRequest)
                .approver(approver)
                .action(ApprovalAction.REJECTED)
                .comments(comments)
                .previousStatus(previousStatus)
                .newStatus(RequestStatus.REJECTED)
                .build();

        approvalHistoryService.createHistory(history);

        return savedRequest;
    }

    @Transactional
    public Request returnRequest(
            Long requestId,
            Long approverId,
            String comments) {

        Request request = requestService.getRequestById(requestId);

        RequestStatus previousStatus = request.getStatus();

        if (previousStatus != RequestStatus.MANAGER_REVIEW
                && previousStatus != RequestStatus.ADMIN_REVIEW) {

            throw new IllegalStateException(
                    "Request cannot be returned from status: "
                            + previousStatus
            );
        }

        User approver = new User();
        approver.setId(approverId);

        request.setStatus(RequestStatus.RETURNED);

        Request savedRequest = requestService.updateRequest(request);

        ApprovalHistory history = ApprovalHistory.builder()
                .request(savedRequest)
                .approver(approver)
                .action(ApprovalAction.RETURNED)
                .comments(comments)
                .previousStatus(previousStatus)
                .newStatus(RequestStatus.RETURNED)
                .build();

        approvalHistoryService.createHistory(history);

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

    private void createHistory(
            Request request,
            User approver,
            ApprovalAction action,
            RequestStatus previousStatus,
            RequestStatus newStatus,
            String comments) {

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
}
