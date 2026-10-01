package com.mahesh.daw.controller;

import com.mahesh.daw.entity.Document;
import com.mahesh.daw.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // Save a document
    @PostMapping
    public ResponseEntity<Document> saveDocument(
            @Valid @RequestBody Document document) {

        Document savedDocument =
                documentService.saveDocument(document);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedDocument);
    }

    // Get document by ID
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                documentService.getDocumentById(id)
        );
    }

    // Delete document
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id) {

        documentService.deleteDocument(id);

        return ResponseEntity.noContent().build();
    }
}
