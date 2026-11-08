package com.mahesh.daw.service;

import com.mahesh.daw.dto.ApprovalHistoryResponse;
import com.mahesh.daw.entity.ApprovalHistory;
import com.mahesh.daw.repository.ApprovalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalHistoryService {

    private final ApprovalHistoryRepository approvalHistoryRepository;

    @Transactional
    public ApprovalHistoryResponse createHistory(ApprovalHistory history) {
        ApprovalHistory saved = approvalHistoryRepository.save(history);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ApprovalHistoryResponse getHistoryById(Long id) {
        ApprovalHistory history = approvalHistoryRepository
                .findWithApproverById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Approval history not found with id: " + id
                        )
                );

        return toResponse(history);
    }

    @Transactional(readOnly = true)
    public List<ApprovalHistoryResponse> getHistoryByRequest(Long requestId) {
        return approvalHistoryRepository
                .findByRequest_IdOrderByCreatedAtAsc(requestId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ApprovalHistoryResponse toResponse(ApprovalHistory history) {
        var approver = history.getApprover();

        ApprovalHistoryResponse.ApproverSummary approverSummary =
                approver == null ? null :
                        new ApprovalHistoryResponse.ApproverSummary(
                                approver.getFirstName(),
                                approver.getLastName(),
                                approver.getEmail()
                        );

        return new ApprovalHistoryResponse(
                history.getId(),
                approverSummary,
                history.getAction(),
                history.getComments(),
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getCreatedAt()
        );
    }
}
