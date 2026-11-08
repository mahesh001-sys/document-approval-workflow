package com.mahesh.daw.service;

import com.mahesh.daw.dto.RequestResponse;
import com.mahesh.daw.entity.*;
import com.mahesh.daw.exception.ForbiddenOperationException;
import com.mahesh.daw.exception.WorkflowConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private Request request;
    private User employee;

    @BeforeEach
    void setUp() {
        employee = User.builder()
                .id(1L)
                .firstName("Employee")
                .lastName("Test")
                .email("employee@example.com")
                .active(true)
                .build();

        RequestType requestType = RequestType.builder()
                .id(1L)
                .name("Leave")
                .build();

        request = Request.builder()
                .id(100L)
                .requestNumber("REQ-001")
                .title("Leave Request")
                .description("Personal leave")
                .status(RequestStatus.SUBMITTED)
                .priority(RequestPriority.MEDIUM)
                .requester(employee)
                .requestType(requestType)
                .build();

        lenient().when(requestService.getRequestById(100L)).thenReturn(request);

        lenient().when(requestService.updateRequest(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private User actor(String email, String roleName, boolean active) {
        return User.builder()
                .id(10L)
                .firstName("Test")
                .lastName(roleName)
                .email(email)
                .active(active)
                .role(Role.builder().id(2L).name(roleName).build())
                .build();
    }

    private void authenticate(String email, String role, boolean active) {
        when(userService.getUserByEmail(email))
                .thenReturn(actor(email, role, active));
    }

    @Test
    void managerReviewShouldChangeStatusToManagerReview() {
        authenticate("manager@example.com", "MANAGER", true);

        RequestResponse result =
                approvalWorkflowService.startManagerReview(
                        100L, "manager@example.com");

        assertEquals("MANAGER_REVIEW", result.status());
        verify(auditLogService).createAuditLog(any(AuditLog.class));
        verify(requestService).updateRequest(request);
    }

    @Test
    void managerReviewShouldRejectInvalidStatus() {
        authenticate("manager@example.com", "MANAGER", true);
        request.setStatus(RequestStatus.DRAFT);

        assertThrows(
                WorkflowConflictException.class,
                () -> approvalWorkflowService.startManagerReview(
                        100L, "manager@example.com")
        );

        verify(requestService, never()).updateRequest(any());
    }

    @Test
    void managerApprovalShouldChangeStatusAndCreateRecords() {
        authenticate("manager@example.com", "MANAGER", true);
        request.setStatus(RequestStatus.MANAGER_REVIEW);

        RequestResponse result =
                approvalWorkflowService.approveManagerRequest(
                        100L, "manager@example.com", "Looks good");

        assertEquals("MANAGER_APPROVED", result.status());

        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));
        verify(auditLogService)
                .createAuditLog(any(AuditLog.class));
        verify(notificationService)
                .createNotification(any(Notification.class));
    }

    @Test
    void managerApprovalShouldRejectWrongRole() {
        authenticate("employee@example.com", "USER", true);
        request.setStatus(RequestStatus.MANAGER_REVIEW);

        assertThrows(
                ForbiddenOperationException.class,
                () -> approvalWorkflowService.approveManagerRequest(
                        100L, "employee@example.com", "Approved")
        );

        verify(requestService, never()).updateRequest(any());
    }

    @Test
    void inactiveManagerShouldNotPerformWorkflowAction() {
        authenticate("manager@example.com", "MANAGER", false);

        assertThrows(
                ForbiddenOperationException.class,
                () -> approvalWorkflowService.startManagerReview(
                        100L, "manager@example.com")
        );

        verify(requestService, never()).updateRequest(any());
    }

    @Test
    void managerRejectShouldChangeStatusToRejected() {
        authenticate("manager@example.com", "MANAGER", true);
        request.setStatus(RequestStatus.MANAGER_REVIEW);

        RequestResponse result =
                approvalWorkflowService.rejectManagerRequest(
                        100L, "manager@example.com", "Insufficient details");

        assertEquals("REJECTED", result.status());
        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));
        verify(notificationService)
                .createNotification(any(Notification.class));
    }

    @Test
    void managerReturnShouldChangeStatusToReturned() {
        authenticate("manager@example.com", "MANAGER", true);
        request.setStatus(RequestStatus.MANAGER_REVIEW);

        RequestResponse result =
                approvalWorkflowService.returnManagerRequest(
                        100L, "manager@example.com", "Please correct the dates");

        assertEquals("RETURNED", result.status());
        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));
    }

    @Test
    void managerCannotRejectDuringAdminReview() {
        authenticate("manager@example.com", "MANAGER", true);
        request.setStatus(RequestStatus.ADMIN_REVIEW);

        assertThrows(
                WorkflowConflictException.class,
                () -> approvalWorkflowService.rejectManagerRequest(
                        100L, "manager@example.com", "Reject")
        );
    }

    @Test
    void adminReviewShouldChangeStatusToAdminReview() {
        authenticate("admin@example.com", "ADMIN", true);
        request.setStatus(RequestStatus.MANAGER_APPROVED);

        RequestResponse result =
                approvalWorkflowService.startAdminReview(
                        100L, "admin@example.com");

        assertEquals("ADMIN_REVIEW", result.status());
        verify(auditLogService).createAuditLog(any(AuditLog.class));
    }

    @Test
    void adminApprovalShouldCompleteRequestAndSetCompletedAt() {
        authenticate("admin@example.com", "ADMIN", true);
        request.setStatus(RequestStatus.ADMIN_REVIEW);

        RequestResponse result =
                approvalWorkflowService.approveAdminRequest(
                        100L, "admin@example.com", "Final approval");

        assertEquals("COMPLETED", result.status());
        assertNotNull(result.completedAt());

        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));
        verify(auditLogService, times(2))
                .createAuditLog(any(AuditLog.class));
        verify(notificationService)
                .createNotification(any(Notification.class));
    }

    @Test
    void adminApprovalShouldRejectWrongRole() {
        authenticate("manager@example.com", "MANAGER", true);
        request.setStatus(RequestStatus.ADMIN_REVIEW);

        assertThrows(
                ForbiddenOperationException.class,
                () -> approvalWorkflowService.approveAdminRequest(
                        100L, "manager@example.com", "Approved")
        );

        verify(requestService, never()).updateRequest(any());
    }

    @Test
    void adminRejectShouldChangeStatusToRejected() {
        authenticate("admin@example.com", "ADMIN", true);
        request.setStatus(RequestStatus.ADMIN_REVIEW);

        RequestResponse result =
                approvalWorkflowService.rejectAdminRequest(
                        100L, "admin@example.com", "Policy violation");

        assertEquals("REJECTED", result.status());
        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));
        verify(notificationService)
                .createNotification(any(Notification.class));
    }

    @Test
    void adminReturnShouldChangeStatusToReturned() {
        authenticate("admin@example.com", "ADMIN", true);
        request.setStatus(RequestStatus.ADMIN_REVIEW);

        RequestResponse result =
                approvalWorkflowService.returnAdminRequest(
                        100L, "admin@example.com", "Additional documents required");

        assertEquals("RETURNED", result.status());
        verify(approvalHistoryService)
                .createHistory(any(ApprovalHistory.class));
    }

    @Test
    void adminCannotApproveBeforeManagerApproval() {
        authenticate("admin@example.com", "ADMIN", true);
        request.setStatus(RequestStatus.SUBMITTED);

        assertThrows(
                WorkflowConflictException.class,
                () -> approvalWorkflowService.approveAdminRequest(
                        100L, "admin@example.com", "Approved")
        );

        verify(requestService, never()).updateRequest(any());
    }
}
