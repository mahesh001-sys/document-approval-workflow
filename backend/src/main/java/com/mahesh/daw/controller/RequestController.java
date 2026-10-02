package com.mahesh.daw.controller;

import com.mahesh.daw.entity.Request;
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

    // Create a new request
    @PostMapping
    public ResponseEntity<Request> createRequest(
            @Valid @RequestBody Request request) {

        Request createdRequest =
                requestService.createRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRequest);
    }

    // Get request by ID - requester only
    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequestById(
            @PathVariable Long id,
            Authentication authentication) {

        Request request =
                requestService.getRequestByIdForEmail(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(request);
    }

    // Get request by request number
    @GetMapping("/number/{requestNumber}")
    public ResponseEntity<Request> getRequestByNumber(
            @PathVariable String requestNumber) {

        return ResponseEntity.ok(
                requestService.getRequestByNumber(
                        requestNumber
                )
        );
    }

    // Get all requests
    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests() {

        return ResponseEntity.ok(
                requestService.getAllRequests()
        );
    }

    // Get requests by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Request>> getRequestsByStatus(
            @PathVariable RequestStatus status) {

        return ResponseEntity.ok(
                requestService.getRequestsByStatus(status)
        );
    }

    // Submit draft request - requester only
    @PostMapping("/{id}/submit")
    public ResponseEntity<Request> submitRequest(
            @PathVariable Long id,
            Authentication authentication) {

        Request submittedRequest =
                requestService.submitRequestForUser(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(submittedRequest);
    }

    // Update request - requester only
    @PutMapping("/{id}")
    public ResponseEntity<Request> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody Request request,
            Authentication authentication) {

        Request updatedRequest =
                requestService.updateRequestForUser(
                        id,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(updatedRequest);
    }

    // Delete request - requester only
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
