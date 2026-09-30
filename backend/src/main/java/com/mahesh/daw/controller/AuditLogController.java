package com.mahesh.daw.controller;

import com.mahesh.daw.entity.AuditLog;
import com.mahesh.daw.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    // Create audit log
    @PostMapping
    public ResponseEntity<AuditLog> createAuditLog(
            @RequestBody AuditLog auditLog) {

        return ResponseEntity.ok(
                auditLogService.createAuditLog(auditLog)
        );
    }

    // Get audit log by ID
    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                auditLogService.getAuditLogById(id)
        );
    }

    // Get audit logs for an entity
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<?> getLogsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        return ResponseEntity.ok(
                auditLogService.getLogsByEntity(
                        entityType,
                        entityId
                )
        );
    }
}
