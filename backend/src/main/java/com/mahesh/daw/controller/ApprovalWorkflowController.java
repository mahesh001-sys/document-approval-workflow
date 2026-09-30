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

    // Submit a request
    @PostMapping("/{requestId}/submit")
    public ResponseEntity<Request> submitRequest(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                approvalWorkflowService.submitRequest(requestId)
        );
    }

    // Manager approves a request
    @PostMapping("/{requestId}/manager/approve")
    public ResponseEntity<Request> managerApprove(
            @PathVariable Long requestId,
            @RequestParam(required = false) String comment) {

        return ResponseEntity.ok(
                approvalWorkflowService.managerApprove(
                        requestId,
                        comment
                )
        );
    }

    // Manager rejects a request
    @PostMapping("/{requestId}/manager/reject")
    public ResponseEntity<Request> managerReject(
            @PathVariable Long requestId,
            @RequestParam(required = false) String comment) {

        return ResponseEntity.ok(
                approvalWorkflowService.managerReject(
                        requestId,
                        comment
                )
        );
    }

    // Admin approves a request
    @PostMapping("/{requestId}/admin/approve")
    public ResponseEntity<Request> adminApprove(
            @PathVariable Long requestId,
            @RequestParam(required = false) String comment) {

        return ResponseEntity.ok(
                approvalWorkflowService.adminApprove(
                        requestId,
                        comment
                )
        );
    }

    // Admin rejects a request
    @PostMapping("/{requestId}/admin/reject")
    public ResponseEntity<Request> adminReject(
            @PathVariable Long requestId,
            @RequestParam(required = false) String comment) {

        return ResponseEntity.ok(
                approvalWorkflowService.adminReject(
                        requestId,
                        comment
                )
        );
    }
}
