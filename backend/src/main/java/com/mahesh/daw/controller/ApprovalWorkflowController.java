package com.mahesh.daw.controller;

import com.mahesh.daw.dto.RequestResponse;
import com.mahesh.daw.service.ApprovalWorkflowService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Validated
public class ApprovalWorkflowController {

    private final ApprovalWorkflowService approvalWorkflowService;

    @PostMapping("/{requestId}/manager/review")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RequestResponse> startManagerReview(
            @PathVariable @Positive Long requestId,
            Authentication authentication) {

        return ResponseEntity.ok(
                approvalWorkflowService.startManagerReview(
                        requestId, authentication.getName()));
    }

    @PostMapping("/{requestId}/manager/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RequestResponse> approveManagerRequest(
            @PathVariable @Positive Long requestId,
            Authentication authentication,
            @RequestParam(required = false)
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.approveManagerRequest(
                        requestId, authentication.getName(), comments));
    }

    @PostMapping("/{requestId}/manager/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RequestResponse> managerRejectRequest(
            @PathVariable @Positive Long requestId,
            Authentication authentication,
            @RequestParam
            @NotBlank(message = "Comments are required")
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.rejectManagerRequest(
                        requestId, authentication.getName(), comments));
    }

    @PostMapping("/{requestId}/manager/return")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RequestResponse> managerReturnRequest(
            @PathVariable @Positive Long requestId,
            Authentication authentication,
            @RequestParam
            @NotBlank(message = "Comments are required")
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.returnManagerRequest(
                        requestId, authentication.getName(), comments));
    }

    @PostMapping("/{requestId}/admin/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RequestResponse> startAdminReview(
            @PathVariable @Positive Long requestId,
            Authentication authentication) {

        return ResponseEntity.ok(
                approvalWorkflowService.startAdminReview(
                        requestId, authentication.getName()));
    }

    @PostMapping("/{requestId}/admin/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RequestResponse> approveAdminRequest(
            @PathVariable @Positive Long requestId,
            Authentication authentication,
            @RequestParam(required = false)
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.approveAdminRequest(
                        requestId, authentication.getName(), comments));
    }

    @PostMapping("/{requestId}/admin/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RequestResponse> adminRejectRequest(
            @PathVariable @Positive Long requestId,
            Authentication authentication,
            @RequestParam
            @NotBlank(message = "Comments are required")
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.rejectAdminRequest(
                        requestId, authentication.getName(), comments));
    }

    @PostMapping("/{requestId}/admin/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RequestResponse> adminReturnRequest(
            @PathVariable @Positive Long requestId,
            Authentication authentication,
            @RequestParam
            @NotBlank(message = "Comments are required")
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.returnAdminRequest(
                        requestId, authentication.getName(), comments));
    }
}
