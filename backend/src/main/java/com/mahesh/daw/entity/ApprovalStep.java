package com.mahesh.daw.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalStep {

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

    @NotNull(message = "Step number is required")
    @Min(value = 1, message = "Step number must be at least 1")
    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStatus status;

    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    @Column(length = 1000)
    private String comments;

    @Column(name = "actioned_at")
    private LocalDateTime actionedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ApprovalStatus.PENDING;
        }
    }
}
