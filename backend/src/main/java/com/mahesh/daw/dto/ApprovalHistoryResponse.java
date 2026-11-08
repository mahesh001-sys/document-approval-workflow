package com.mahesh.daw.dto;

import com.mahesh.daw.entity.ApprovalAction;
import com.mahesh.daw.entity.RequestStatus;

import java.time.LocalDateTime;

public record ApprovalHistoryResponse(
        Long id,
        ApproverSummary approver,
        ApprovalAction action,
        String comments,
        RequestStatus previousStatus,
        RequestStatus newStatus,
        LocalDateTime createdAt
) {
    public record ApproverSummary(
            String firstName,
            String lastName,
            String email
    ) {}
}
