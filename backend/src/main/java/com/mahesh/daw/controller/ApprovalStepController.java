 package com.mahesh.daw.controller;

import com.mahesh.daw.entity.ApprovalStep;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.service.ApprovalStepService;
import com.mahesh.daw.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-steps")
@RequiredArgsConstructor
public class ApprovalStepController {

    private final ApprovalStepService approvalStepService;
    private final RequestService requestService;

    // Create approval step
    @PostMapping
    public ResponseEntity<ApprovalStep> createApprovalStep(
            @Valid @RequestBody ApprovalStep approvalStep) {

        ApprovalStep createdStep =
                approvalStepService.createApprovalStep(approvalStep);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdStep);
    }

    // Get approval step by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalStep> getApprovalStepById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                approvalStepService.getApprovalStepById(id)
        );
    }

    // Get all approval steps for a request
    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<ApprovalStep>> getStepsByRequest(
            @PathVariable Long requestId) {

        Request request = requestService.getRequestById(requestId);

        return ResponseEntity.ok(
                approvalStepService.getStepsByRequest(request)
        );
    }

    // Get approval step by request and step number
    @GetMapping("/request/{requestId}/step/{stepNumber}")
    public ResponseEntity<ApprovalStep> getStepByRequestAndNumber(
            @PathVariable Long requestId,
            @PathVariable Integer stepNumber) {

        Request request = requestService.getRequestById(requestId);

        return ResponseEntity.ok(
                approvalStepService.getStepByRequestAndNumber(
                        request,
                        stepNumber
                )
        );
    }

    // Update approval step
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalStep> updateApprovalStep(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalStep approvalStep) {

        ApprovalStep existingStep =
                approvalStepService.getApprovalStepById(id);

        approvalStep.setId(existingStep.getId());

        ApprovalStep updatedStep =
                approvalStepService.updateApprovalStep(approvalStep);

        return ResponseEntity.ok(updatedStep);
    }
}
