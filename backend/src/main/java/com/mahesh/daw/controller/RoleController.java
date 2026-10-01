package com.mahesh.daw.controller;

import com.mahesh.daw.entity.Role;
import com.mahesh.daw.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // Create role
    @PostMapping
    public ResponseEntity<Role> createRole(
            @Valid @RequestBody Role role) {

        Role createdRole = roleService.createRole(role);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRole);
    }

    // Get role by ID
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                roleService.getRoleById(id)
        );
    }

    // Get role by name
    @GetMapping("/name/{name}")
    public ResponseEntity<Role> getRoleByName(
            @PathVariable String name) {

        return ResponseEntity.ok(
                roleService.getRoleByName(name)
        );
    }

    // Get all roles
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {

        return ResponseEntity.ok(
                roleService.getAllRoles()
        );
    }
}
