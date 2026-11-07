package com.mahesh.daw.config;

import com.mahesh.daw.entity.Role;
import com.mahesh.daw.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        List<String> defaultRoles = List.of(
                "USER",
                "MANAGER",
                "ADMIN"
        );

        for (String roleName : defaultRoles) {

            if (!roleRepository.existsByName(roleName)) {

                Role role = Role.builder()
                        .name(roleName)
                        .build();

                roleRepository.save(role);

                System.out.println(
                        "Created default role: " + roleName
                );
            }
        }
    }
}
