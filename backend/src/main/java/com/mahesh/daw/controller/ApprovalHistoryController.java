package com.mahesh.daw.controller;

import com.mahesh.daw.dto.ApprovalHistoryResponse;
import com.mahesh.daw.entity.ApprovalHistory;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.service.ApprovalHistoryService;
import com.mahesh.daw.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-history")
@RequiredArgsConstructor
public class ApprovalHistoryController {

    private final ApprovalHistoryService approvalHistoryService;
    private final RequestService requestService;

    // Create approval history through trusted workflow services only.
    @PostMapping
    public ResponseEntity<ApprovalHistoryResponse> createHistory(
            @Valid @RequestBody ApprovalHistory history) {

        return ResponseEntity.ok(
                approvalHistoryService.createHistory(history)
        );
    }

    // Get approval history by ID.
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalHistoryResponse> getHistoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                approvalHistoryService.getHistoryById(id)
        );
    }

    // Employees can read their own request history.
    // Managers and admins can read request history as well.
    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<ApprovalHistoryResponse>> getHistoryByRequest(
            @PathVariable Long requestId,
            Authentication authentication) {

        boolean isManagerOrAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_MANAGER")
                        || authority.getAuthority().equals("ROLE_ADMIN")
                );

        Request request;

        if (isManagerOrAdmin) {
            request = requestService.getRequestById(requestId);
        } else {
            request = requestService.getRequestByIdForEmail(
                    requestId,
                    authentication.getName()
            );
        }

        return ResponseEntity.ok(
                approvalHistoryService.getHistoryByRequest(request.getId())
        );
    }
}
