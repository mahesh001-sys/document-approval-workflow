package com.mahesh.daw.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService =
            mock(JwtService.class);

    private final UserDetailsService userDetailsService =
            mock(UserDetailsService.class);

    private final JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(
                    jwtService,
                    userDetailsService
            );

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validJwtShouldAuthenticateUser()
            throws Exception {

        String token = "valid-jwt-token";

        UserDetails userDetails =
                User.withUsername("test@example.com")
                        .password("encoded-password")
                        .roles("USER")
                        .build();

        when(jwtService.extractUsername(token))
                .thenReturn("test@example.com");

        when(userDetailsService.loadUserByUsername(
                "test@example.com"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                token,
                userDetails))
                .thenReturn(true);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertEquals(
                "test@example.com",
                authentication.getName()
        );

        assertTrue(
                authentication.isAuthenticated()
        );

        verify(filterChain).doFilter(
                request,
                response
        );
    }

    @Test
    void requestWithoutJwtShouldRemainUnauthenticated()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNull(authentication);

        verify(filterChain).doFilter(
                request,
                response
        );
    }

    @Test
    void invalidJwtShouldRemainUnauthenticated()
            throws Exception {

        String token = "invalid-jwt-token";

        when(jwtService.extractUsername(token))
                .thenThrow(
                        new RuntimeException("Invalid JWT")
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNull(authentication);

        verify(filterChain).doFilter(
                request,
                response
        );
    }
}
