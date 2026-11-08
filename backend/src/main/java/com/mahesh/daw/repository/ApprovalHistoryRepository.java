package com.mahesh.daw.repository;

import com.mahesh.daw.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalHistoryRepository
        extends JpaRepository<ApprovalHistory, Long> {

    @EntityGraph(attributePaths = "approver")
    List<ApprovalHistory> findByRequest_IdOrderByCreatedAtAsc(Long requestId);

    @EntityGraph(attributePaths = "approver")
    Optional<ApprovalHistory> findWithApproverById(Long id);
}
