package com.mahesh.daw.controller;

import com.mahesh.daw.dto.RequestCreateRequest;
import com.mahesh.daw.dto.RequestResponse;
import com.mahesh.daw.dto.RequestUpdateRequest;
import com.mahesh.daw.entity.RequestStatus;
import com.mahesh.daw.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponse> createRequest(
            @Valid @RequestBody RequestCreateRequest request,
            Authentication authentication) {

        RequestResponse createdRequest =
                requestService.createRequest(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponse> getRequestById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                requestService.getRequestResponseByIdForEmail(
                        id,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/number/{requestNumber}")
    public ResponseEntity<RequestResponse> getRequestByNumber(
            @PathVariable String requestNumber,
            Authentication authentication) {

        return ResponseEntity.ok(
                requestService.getRequestResponseByNumberForEmail(
                        requestNumber,
                        authentication.getName()
                )
        );
    }

    // All requests - manager view
    @GetMapping
    public ResponseEntity<List<RequestResponse>> getAllRequests() {

        return ResponseEntity.ok(
                requestService.getAllRequests()
        );
    }

    // Requests belonging to the authenticated employee
    @GetMapping("/my")
    public ResponseEntity<List<RequestResponse>> getMyRequests(
            Authentication authentication) {

        return ResponseEntity.ok(
                requestService.getMyRequests(authentication.getName())
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequestResponse>> getRequestsByStatus(
            @PathVariable RequestStatus status) {

        return ResponseEntity.ok(
                requestService.getRequestsByStatus(status)
        );
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<RequestResponse> submitRequest(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                requestService.submitRequestForUser(
                        id,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestResponse> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody RequestUpdateRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                requestService.updateRequestForUser(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable Long id,
            Authentication authentication) {

        requestService.deleteRequestForUser(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
