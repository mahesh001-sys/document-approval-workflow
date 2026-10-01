package com.mahesh.daw.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Request is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @NotBlank(message = "File name cannot be blank")
    @Size(max = 255, message = "File name cannot exceed 255 characters")
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @NotBlank(message = "File type cannot be blank")
    @Size(max = 100, message = "File type cannot exceed 100 characters")
    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;

    @NotNull(message = "File size is required")
    @PositiveOrZero(message = "File size cannot be negative")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @NotBlank(message = "File path cannot be blank")
    @Size(max = 500, message = "File path cannot exceed 500 characters")
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @NotNull(message = "Uploaded by user is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
