package com.mahesh.daw.repository;

import com.mahesh.daw.entity.AuditLog;
import com.mahesh.daw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserOrderByCreatedAtDesc(User user);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtAsc(
            String entityType,
            Long entityId
    );
}
