package com.mahesh.daw.controller;

import com.mahesh.daw.entity.Request;
import com.mahesh.daw.service.ApprovalWorkflowService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Validated
public class ApprovalWorkflowController {

    private final ApprovalWorkflowService approvalWorkflowService;

    @PostMapping("/{requestId}/submit")
    public ResponseEntity<Request> submitRequest(
            @PathVariable @Positive Long requestId) {

        return ResponseEntity.ok(
                approvalWorkflowService.submitRequest(requestId)
        );
    }

    @PostMapping("/{requestId}/manager/review")
    public ResponseEntity<Request> startManagerReview(
            @PathVariable @Positive Long requestId) {

        return ResponseEntity.ok(
                approvalWorkflowService.startManagerReview(requestId)
        );
    }

    @PostMapping("/{requestId}/manager/approve")
    public ResponseEntity<Request> approveManagerRequest(
            @PathVariable @Positive Long requestId,
            @RequestParam @Positive Long approverId,
            @RequestParam(required = false)
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.approveManagerRequest(
                        requestId,
                        approverId,
                        comments
                )
        );
    }

    @PostMapping("/{requestId}/admin/review")
    public ResponseEntity<Request> startAdminReview(
            @PathVariable @Positive Long requestId) {

        return ResponseEntity.ok(
                approvalWorkflowService.startAdminReview(requestId)
        );
    }

    @PostMapping("/{requestId}/admin/approve")
    public ResponseEntity<Request> approveAdminRequest(
            @PathVariable @Positive Long requestId,
            @RequestParam @Positive Long approverId,
            @RequestParam(required = false)
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.approveAdminRequest(
                        requestId,
                        approverId,
                        comments
                )
        );
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Request> rejectRequest(
            @PathVariable @Positive Long requestId,
            @RequestParam @Positive Long approverId,
            @RequestParam(required = false)
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.rejectRequest(
                        requestId,
                        approverId,
                        comments
                )
        );
    }

    @PostMapping("/{requestId}/return")
    public ResponseEntity<Request> returnRequest(
            @PathVariable @Positive Long requestId,
            @RequestParam @Positive Long approverId,
            @RequestParam(required = false)
            @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
            String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.returnRequest(
                        requestId,
                        approverId,
                        comments
                )
        );
    }
}
