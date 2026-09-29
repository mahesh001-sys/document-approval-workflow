package com.mahesh.daw.repository;

import com.mahesh.daw.entity.ApprovalHistory;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    List<ApprovalHistory> findByRequestOrderByCreatedAtAsc(Request request);

    List<ApprovalHistory> findByApproverOrderByCreatedAtDesc(User approver);
}
