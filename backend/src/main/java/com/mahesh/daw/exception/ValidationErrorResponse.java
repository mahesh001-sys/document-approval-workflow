package com.mahesh.daw.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class ValidationErrorResponse {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private Map<String, String> fieldErrors;
}
