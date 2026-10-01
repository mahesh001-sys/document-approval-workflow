package com.mahesh.daw.config;

import com.mahesh.daw.security.JwtAuthenticationFilter;
import com.mahesh.daw.security.RestAccessDeniedHandler;
import com.mahesh.daw.security.RestAuthenticationEntryPoint;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfigTest.TestSecurityConfiguration.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @MockBean
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @Configuration
    static class TestSecurityConfiguration {

        @Bean
        SecurityFilterChain testSecurityFilterChain(
                HttpSecurity http) throws Exception {

            http
                    .csrf(csrf -> csrf.disable())

                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().authenticated()
                    );

            return http.build();
        }
    }

    @Test
    void protectedEndpointWithoutTokenShouldBeUnauthorized()
            throws Exception {

        mockMvc.perform(
                get("/api/requests")
        )
        .andExpect(status().isUnauthorized());
    }
}
