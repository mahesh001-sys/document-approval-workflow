package com.mahesh.daw.controller;

import com.mahesh.daw.entity.RequestType;
import com.mahesh.daw.service.RequestTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-types")
@RequiredArgsConstructor
public class RequestTypeController {

    private final RequestTypeService requestTypeService;

    // Create request type
    @PostMapping
    public ResponseEntity<RequestType> createRequestType(
            @Valid @RequestBody RequestType requestType) {

        RequestType createdRequestType =
                requestTypeService.createRequestType(requestType);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRequestType);
    }

    // Get request type by ID
    @GetMapping("/{id}")
    public ResponseEntity<RequestType> getRequestTypeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestTypeService.getRequestTypeById(id)
        );
    }

    // Get request type by name
    @GetMapping("/name/{name}")
    public ResponseEntity<RequestType> getRequestTypeByName(
            @PathVariable String name) {

        return ResponseEntity.ok(
                requestTypeService.getRequestTypeByName(name)
        );
    }

    // Get all request types
    @GetMapping
    public ResponseEntity<List<RequestType>> getAllRequestTypes() {

        return ResponseEntity.ok(
                requestTypeService.getAllRequestTypes()
        );
    }

    // Get active request types
    @GetMapping("/active")
    public ResponseEntity<List<RequestType>> getActiveRequestTypes() {

        return ResponseEntity.ok(
                requestTypeService.getActiveRequestTypes()
        );
    }
}
