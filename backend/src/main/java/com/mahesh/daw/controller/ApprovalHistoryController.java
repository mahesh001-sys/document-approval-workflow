package com.mahesh.daw.controller;

import com.mahesh.daw.entity.ApprovalHistory;
import com.mahesh.daw.service.ApprovalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval-history")
@RequiredArgsConstructor
public class ApprovalHistoryController {

    private final ApprovalHistoryService approvalHistoryService;

    // Create approval history
    @PostMapping
    public ResponseEntity<ApprovalHistory> createHistory(
            @RequestBody ApprovalHistory history) {

        return ResponseEntity.ok(
                approvalHistoryService.createHistory(history)
        );
    }

    // Get approval history by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalHistory> getHistoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                approvalHistoryService.getHistoryById(id)
        );
    }

    // Get approval history for a request
    @GetMapping("/request/{requestId}")
    public ResponseEntity<?> getHistoryByRequest(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                approvalHistoryService
                        .getHistoryByRequest(
                                new com.mahesh.daw.entity.Request()
                        )
        );
    }
}
