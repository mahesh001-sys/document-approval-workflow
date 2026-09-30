package com.mahesh.daw.controller;

import com.mahesh.daw.entity.Request;
import com.mahesh.daw.service.ApprovalWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class ApprovalWorkflowController {

    private final ApprovalWorkflowService approvalWorkflowService;

    @PostMapping("/{requestId}/submit")
    public ResponseEntity<Request> submitRequest(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                approvalWorkflowService.submitRequest(requestId)
        );
    }

    @PostMapping("/{requestId}/manager/review")
    public ResponseEntity<Request> startManagerReview(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                approvalWorkflowService.startManagerReview(requestId)
        );
    }

    @PostMapping("/{requestId}/manager/approve")
    public ResponseEntity<Request> approveManagerRequest(
            @PathVariable Long requestId,
            @RequestParam Long approverId,
            @RequestParam(required = false) String comments) {

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
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                approvalWorkflowService.startAdminReview(requestId)
        );
    }

    @PostMapping("/{requestId}/admin/approve")
    public ResponseEntity<Request> approveAdminRequest(
            @PathVariable Long requestId,
            @RequestParam Long approverId,
            @RequestParam(required = false) String comments) {

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
            @PathVariable Long requestId,
            @RequestParam Long approverId,
            @RequestParam(required = false) String comments) {

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
            @PathVariable Long requestId,
            @RequestParam Long approverId,
            @RequestParam(required = false) String comments) {

        return ResponseEntity.ok(
                approvalWorkflowService.returnRequest(
                        requestId,
                        approverId,
                        comments
                )
        );
    }
}
