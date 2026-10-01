package com.mahesh.daw.controller;

import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.RequestStatus;
import com.mahesh.daw.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        Request createdRequest = requestService.createRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRequest);
    }

    // Get request by ID
    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequestById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestService.getRequestById(id)
        );
    }

    // Get request by request number
    @GetMapping("/number/{requestNumber}")
    public ResponseEntity<Request> getRequestByNumber(
            @PathVariable String requestNumber) {

        return ResponseEntity.ok(
                requestService.getRequestByNumber(requestNumber)
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

    // Submit a draft request
    @PostMapping("/{id}/submit")
    public ResponseEntity<Request> submitRequest(
            @PathVariable Long id) {

        Request submittedRequest =
                requestService.submitRequest(id);

        return ResponseEntity.ok(submittedRequest);
    }

    // Update request
    @PutMapping("/{id}")
    public ResponseEntity<Request> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody Request request) {

        Request existingRequest =
                requestService.getRequestById(id);

        request.setId(existingRequest.getId());

        Request updatedRequest =
                requestService.updateRequest(request);

        return ResponseEntity.ok(updatedRequest);
    }

    // Delete request
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable Long id) {

        requestService.deleteRequest(id);

        return ResponseEntity.noContent().build();
    }
}
