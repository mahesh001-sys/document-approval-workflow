package com.mahesh.daw.dto;

import com.mahesh.daw.entity.Request;

import java.time.LocalDateTime;

public record RequestResponse(
        Long id,
        String requestNumber,
        String title,
        String description,
        String status,
        String priority,
        Long requestTypeId,
        String requestTypeName,
        Long requesterId,
        String requesterFirstName,
        String requesterLastName,
        String requesterEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime submittedAt,
        LocalDateTime completedAt
) {

    public static RequestResponse from(Request request) {
        return new RequestResponse(
                request.getId(),
                request.getRequestNumber(),
                request.getTitle(),
                request.getDescription(),
                request.getStatus().name(),
                request.getPriority().name(),
                request.getRequestType().getId(),
                request.getRequestType().getName(),
                request.getRequester().getId(),
                request.getRequester().getFirstName(),
                request.getRequester().getLastName(),
                request.getRequester().getEmail(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getSubmittedAt(),
                request.getCompletedAt()
        );
    }
}
