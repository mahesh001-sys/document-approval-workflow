package com.mahesh.daw.service;

import com.mahesh.daw.entity.ApprovalStatus;
import com.mahesh.daw.entity.ApprovalStep;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.repository.ApprovalStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalStepService {

    private final ApprovalStepRepository approvalStepRepository;

    public ApprovalStep createApprovalStep(ApprovalStep approvalStep) {

        if (approvalStepRepository.existsByRequestAndStepNumber(
                approvalStep.getRequest(),
                approvalStep.getStepNumber())) {

            throw new IllegalArgumentException(
                    "Approval step already exists for request: "
                            + approvalStep.getRequest().getId()
                            + " and step number: "
                            + approvalStep.getStepNumber()
            );
        }

        if (approvalStep.getStatus() == null) {
            approvalStep.setStatus(ApprovalStatus.PENDING);
        }

        return approvalStepRepository.save(approvalStep);
    }

    public ApprovalStep getApprovalStepById(Long id) {

        return approvalStepRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Approval step not found with id: " + id
                        )
                );
    }

    public List<ApprovalStep> getStepsByRequest(Request request) {

        return approvalStepRepository
                .findByRequestOrderByStepNumberAsc(request);
    }

    public List<ApprovalStep> getPendingApprovals(User approver) {

        return approvalStepRepository.findByApproverAndStatus(
                approver,
                ApprovalStatus.PENDING
        );
    }

    public ApprovalStep getStepByRequestAndNumber(
            Request request,
            Integer stepNumber) {

        return approvalStepRepository
                .findByRequestAndStepNumber(request, stepNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Approval step not found for request: "
                                        + request.getId()
                                        + " and step: "
                                        + stepNumber
                        )
                );
    }

    public ApprovalStep updateApprovalStep(
            ApprovalStep approvalStep) {

        return approvalStepRepository.save(approvalStep);
    }
}
