package com.mahesh.daw.config;

import com.mahesh.daw.security.JwtAuthenticationFilter;
import com.mahesh.daw.security.RestAccessDeniedHandler;
import com.mahesh.daw.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:5173}")
            String allowedOrigins) {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isEmpty())
                        .toList()
        );

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type"
        ));

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Login
                        .requestMatchers("/api/auth/login").permitAll()

                        // Public registration only
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                        // User and role administration
                        .requestMatchers("/api/users", "/api/users/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/roles", "/api/roles/**")
                        .hasRole("ADMIN")

                        // Request queues
                        .requestMatchers(HttpMethod.GET, "/api/requests")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/requests/status/**")
                        .hasAnyRole("MANAGER", "ADMIN")

                        // Employee's own requests
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/requests/my",
                                "/api/requests/number/**"
                        ).authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/requests/*")
                        .authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/requests")
                        .hasRole("USER")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/requests/*/submit"
                        ).hasRole("USER")

                        .requestMatchers(HttpMethod.PUT, "/api/requests/*")
                        .hasRole("USER")

                        .requestMatchers(HttpMethod.DELETE, "/api/requests/*")
                        .hasRole("USER")

                        // Request types: everyone authenticated can read;
                        // only administrators can create.
                        .requestMatchers(HttpMethod.GET, "/api/request-types/**")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/request-types/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/request-types/**")
                        .denyAll()

                        // Approval steps: managers/admins can read;
                        // only admins can configure them.
                        .requestMatchers(HttpMethod.GET, "/api/approval-steps/**")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(
                                HttpMethod.POST, "/api/approval-steps/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/api/approval-steps/**"
                        ).hasRole("ADMIN")
                        .requestMatchers("/api/approval-steps/**")
                        .denyAll()

                        // Employees can request history; ownership is checked
                        // in the controller. Managers and admins retain access.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/approval-history/request/*"
                        ).authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/approval-history/**")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/approval-history/**")
                        .denyAll()

                        // Audit logs are admin-readable and never directly writable.
                        .requestMatchers(HttpMethod.GET, "/api/audit-logs/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/audit-logs/**")
                        .denyAll()

                        // These APIs remain disabled until resource ownership
                        // is enforced in their service methods.
                        .requestMatchers("/api/documents/**").denyAll()
                        .requestMatchers("/api/comments/**").denyAll()
                        .requestMatchers("/api/notifications/**").denyAll()

                        // Manager workflow
                        .requestMatchers("/api/workflow/*/manager/**")
                        .hasRole("MANAGER")

                        // Admin workflow
                        .requestMatchers("/api/workflow/*/admin/**")
                        .hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
