package com.mahesh.daw.service;

import com.mahesh.daw.entity.Role;
import com.mahesh.daw.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(Role role) {

        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException(
                    "Role already exists: " + role.getName()
            );
        }

        return roleRepository.save(role);
    }

    public Role getRoleById(Long id) {

        return roleRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found with id: " + id
                        )
                );
    }

    public Role getRoleByName(String name) {

        return roleRepository.findByName(name)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: " + name
                        )
                );
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
