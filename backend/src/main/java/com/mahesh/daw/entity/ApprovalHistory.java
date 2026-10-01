package com.mahesh.daw.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Request is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @NotNull(message = "Approver is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @NotNull(message = "Approval action is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApprovalAction action;

    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    @Column(length = 1000)
    private String comments;

    @NotNull(message = "Previous status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false, length = 50)
    private RequestStatus previousStatus;

    @NotNull(message = "New status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 50)
    private RequestStatus newStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
