package com.mahesh.daw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RequestCreateRequest(

        @NotBlank(message = "Request title cannot be blank")
        @Size(max = 200, message = "Request title cannot exceed 200 characters")
        String title,

        @Size(max = 5000, message = "Description cannot exceed 5000 characters")
        String description,

        @NotNull(message = "Request type is required")
        @Positive(message = "Request type ID must be positive")
        Long requestTypeId
) {
}
