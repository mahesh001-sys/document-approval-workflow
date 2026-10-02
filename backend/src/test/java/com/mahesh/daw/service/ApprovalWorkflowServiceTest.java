package com.mahesh.daw.service;

import com.mahesh.daw.entity.*;
import com.mahesh.daw.repository.RequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalWorkflowServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private UserService userService;

    @Mock
    private ApprovalHistoryService approvalHistoryService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApprovalWorkflowService approvalWorkflowService;

    @Test
    void submitRequestShouldChangeStatusToSubmitted() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.DRAFT)
                .build();

        when(requestService.getRequestById(1L))
                .thenReturn(request);

        when(requestService.updateRequest(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result =
                approvalWorkflowService.submitRequest(1L);

        assertEquals(
                RequestStatus.SUBMITTED,
                result.getStatus()
        );

        verify(requestService)
                .updateRequest(request);

        verify(auditLogService)
                .createAuditLog(any(AuditLog.class));
    }

    @Test
    void managerReviewShouldChangeStatusToManagerReview() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.SUBMITTED)
                .build();

        when(requestService.getRequestById(1L))
                .thenReturn(request);

        when(requestService.updateRequest(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result =
                approvalWorkflowService.startManagerReview(1L);

        assertEquals(
                RequestStatus.MANAGER_REVIEW,
                result.getStatus()
        );

        verify(requestService)
                .updateRequest(request);

        verify(auditLogService)
                .createAuditLog(any(AuditLog.class));
    }

    @Test
    void managerReviewShouldRejectInvalidStatus() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.DRAFT)
                .build();

        when(requestService.getRequestById(1L))
                .thenReturn(request);

        assertThrows(
                IllegalStateException.class,
                () -> approvalWorkflowService.startManagerReview(1L)
        );

        verify(requestService, never())
                .updateRequest(any(Request.class));
    }

    @Test
    void managerApprovalShouldChangeStatusToManagerApproved() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.MANAGER_REVIEW)
                .build();

        User approver = User.builder()
                .id(10L)
                .email("manager@example.com")
                .build();

        when(requestService.getRequestById(1L))
                .thenReturn(request);

        when(userService.getUserById(10L))
                .thenReturn(approver);

        when(requestService.updateRequest(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result =
                approvalWorkflowService.approveManagerRequest(
                        1L,
                        10L,
                        "Approved"
                );

        assertEquals(
                RequestStatus.MANAGER_APPROVED,
                result.getStatus()
        );

        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));

        verify(auditLogService)
                .createAuditLog(any(AuditLog.class));
    }

    @Test
    void adminReviewShouldChangeStatusToAdminReview() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.MANAGER_APPROVED)
                .build();

        when(requestService.getRequestById(1L))
                .thenReturn(request);

        when(requestService.updateRequest(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result =
                approvalWorkflowService.startAdminReview(1L);

        assertEquals(
                RequestStatus.ADMIN_REVIEW,
                result.getStatus()
        );

        verify(auditLogService)
                .createAuditLog(any(AuditLog.class));
    }

    @Test
    void adminApprovalShouldCompleteRequest() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.ADMIN_REVIEW)
                .build();

        User approver = User.builder()
                .id(20L)
                .email("admin@example.com")
                .build();

        when(requestService.getRequestById(1L))
                .thenReturn(request);

        when(userService.getUserById(20L))
                .thenReturn(approver);

        when(requestService.updateRequest(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result =
                approvalWorkflowService.approveAdminRequest(
                        1L,
                        20L,
                        "Final approval"
                );

        assertEquals(
                RequestStatus.COMPLETED,
                result.getStatus()
        );

        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));

        verify(auditLogService, atLeastOnce())
                .createAuditLog(any(AuditLog.class));
    }

    @Test
    void rejectRequestShouldChangeStatusToRejected() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.MANAGER_REVIEW)
                .build();

        User approver = User.builder()
                .id(10L)
                .email("manager@example.com")
                .build();

        when(requestService.getRequestById(1L))
                .thenReturn(request);

        when(userService.getUserById(10L))
                .thenReturn(approver);

        when(requestService.updateRequest(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result =
                approvalWorkflowService.rejectRequest(
                        1L,
                        10L,
                        "Rejected"
                );

        assertEquals(
                RequestStatus.REJECTED,
                result.getStatus()
        );

        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));
    }

    @Test
    void returnRequestShouldChangeStatusToReturned() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.MANAGER_REVIEW)
                .build();

        User approver = User.builder()
                .id(10L)
                .email("manager@example.com")
                .build();

        when(requestService.getRequestById(1L))
                .thenReturn(request);

        when(userService.getUserById(10L))
                .thenReturn(approver);

        when(requestService.updateRequest(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result =
                approvalWorkflowService.returnRequest(
                        1L,
                        10L,
                        "Please correct the request"
                );

        assertEquals(
                RequestStatus.RETURNED,
                result.getStatus()
        );

        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));
    }
}
