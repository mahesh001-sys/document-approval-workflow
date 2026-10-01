package com.mahesh.daw.controller;

import com.mahesh.daw.dto.AuthRequest;
import com.mahesh.daw.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private final AuthenticationManager authenticationManager =
            mock(AuthenticationManager.class);

    private final JwtService jwtService =
            mock(JwtService.class);

    private final AuthController authController =
            new AuthController(authenticationManager, jwtService);

    private final MockMvc mockMvc =
            MockMvcBuilders
                    .standaloneSetup(authController)
                    .build();

    @Test
    void loginShouldReturnJwtToken() throws Exception {

        Authentication authentication = mock(Authentication.class);

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("test@example.com")
                        .password("encodedPassword")
                        .authorities("ROLE_USER")
                        .build();

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("test-jwt-token");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@example.com",
                                    "password": "Password123"
                                }
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(content().string("test-jwt-token"));
    }
}
