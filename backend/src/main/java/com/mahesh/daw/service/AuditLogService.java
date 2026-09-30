package com.mahesh.daw.service;

import com.mahesh.daw.entity.AuditLog;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog createAuditLog(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    public AuditLog getAuditLogById(Long id) {

        return auditLogRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Audit log not found with id: " + id
                        )
                );
    }

    public List<AuditLog> getLogsByUser(User user) {

        return auditLogRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    public List<AuditLog> getLogsByEntity(
            String entityType,
            Long entityId) {

        return auditLogRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtAsc(
                        entityType,
                        entityId
                );
    }
}
