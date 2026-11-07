package com.mahesh.daw.exception;

import com.mahesh.daw.dto.UserRegistrationRequest;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void resourceNotFoundShouldReturn404() {

        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(
                        new ResourceNotFoundException("User not found")
                );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("User not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void duplicateResourceShouldReturn409() {

        ResponseEntity<ErrorResponse> response =
                handler.handleDuplicate(
                        new DuplicateResourceException("Email already exists")
                );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Email already exists", response.getBody().getMessage());
    }

    @Test
    void workflowConflictShouldReturn409() {

        ResponseEntity<ErrorResponse> response =
                handler.handleWorkflowConflict(
                        new WorkflowConflictException("Invalid workflow status")
                );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Invalid workflow status", response.getBody().getMessage());
    }

    @Test
    void forbiddenOperationShouldReturn403() {

        ResponseEntity<ErrorResponse> response =
                handler.handleForbidden(
                        new ForbiddenOperationException("Access denied")
                );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    void illegalArgumentShouldReturn400() {

        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalArgumentException(
                        new IllegalArgumentException("Comment cannot be blank")
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Comment cannot be blank", response.getBody().getMessage());
    }

    @Test
    void validationFailureShouldReturn400WithFieldErrors() throws Exception {

        Method method = getClass().getDeclaredMethod(
                "validationTarget",
                UserRegistrationRequest.class
        );

        MethodParameter parameter = new MethodParameter(method, 0);

        BindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError(
                        "request",
                        "email",
                        "Please provide a valid email address"
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ValidationErrorResponse> response =
                handler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Failed", response.getBody().getError());
        assertEquals(
                "Please provide a valid email address",
                response.getBody().getFieldErrors().get("email")
        );
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void badCredentialsShouldReturn401WithoutRevealingDetails() {

        ResponseEntity<ErrorResponse> response =
                handler.handleBadCredentials(
                        new BadCredentialsException("Bad credentials")
                );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals(
                "Invalid email or password",
                response.getBody().getMessage()
        );
    }

    @Test
    void unexpectedExceptionShouldReturn500WithoutExposingDetails() {

        ResponseEntity<ErrorResponse> response =
                handler.handleGenericException(
                        new RuntimeException("Sensitive internal database details")
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertEquals(500, response.getBody().getStatus());

        assertEquals(
                "An unexpected error occurred",
                response.getBody().getMessage()
        );

        assertFalse(
                response.getBody().getMessage()
                        .contains("Sensitive internal database details")
        );
    }

    private void validationTarget(
            @Valid UserRegistrationRequest request) {
    }
}
