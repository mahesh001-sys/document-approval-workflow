package com.mahesh.daw.repository;

import com.mahesh.daw.entity.ApprovalStatus;
import com.mahesh.daw.entity.ApprovalStep;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {

    List<ApprovalStep> findByRequestOrderByStepNumberAsc(Request request);

    List<ApprovalStep> findByApproverAndStatus(
            User approver,
            ApprovalStatus status
    );

    Optional<ApprovalStep> findByRequestAndStepNumber(
            Request request,
            Integer stepNumber
    );

    boolean existsByRequestAndStepNumber(
            Request request,
            Integer stepNumber
    );
}
