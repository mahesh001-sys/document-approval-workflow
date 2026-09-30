package com.mahesh.daw.service;

import com.mahesh.daw.entity.ApprovalHistory;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.repository.ApprovalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalHistoryService {

    private final ApprovalHistoryRepository approvalHistoryRepository;

    public ApprovalHistory createHistory(ApprovalHistory history) {
        return approvalHistoryRepository.save(history);
    }

    public ApprovalHistory getHistoryById(Long id) {

        return approvalHistoryRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Approval history not found with id: " + id
                        )
                );
    }

    public List<ApprovalHistory> getHistoryByRequest(Request request) {

        return approvalHistoryRepository
                .findByRequestOrderByCreatedAtAsc(request);
    }

    public List<ApprovalHistory> getHistoryByApprover(User approver) {

        return approvalHistoryRepository
                .findByApproverOrderByCreatedAtDesc(approver);
    }
}
