package com.mahesh.daw.controller;

import com.mahesh.daw.dto.UserRegistrationRequest;
import com.mahesh.daw.dto.UserResponse;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Public registration: the server assigns the USER role.
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        User createdUser = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(createdUser));
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                toResponse(userService.getUserById(id))
        );
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(
                toResponse(userService.getUserByEmail(email))
        );
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(UserController::toResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    // Check whether an email already exists
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> existsByEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(
                userService.existsByEmail(email)
        );
    }

    private static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().getName(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
