package com.mahesh.daw.service;

import com.mahesh.daw.dto.UserRegistrationRequest;
import com.mahesh.daw.exception.DuplicateResourceException;
import com.mahesh.daw.exception.ResourceNotFoundException;
import com.mahesh.daw.entity.Role;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.repository.RoleRepository;
import com.mahesh.daw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRegistrationRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "User already exists with email: " + request.email()
            );
        }

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Default USER role was not found"
                        )
                );

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(defaultRole)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
