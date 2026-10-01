package com.mahesh.daw.config;

import com.mahesh.daw.security.JwtAuthenticationFilter;
import com.mahesh.daw.security.RestAccessDeniedHandler;
import com.mahesh.daw.security.RestAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @MockBean
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @Test
    void protectedEndpointWithoutTokenShouldBeUnauthorized()
            throws Exception {

        mockMvc.perform(
                get("/api/requests")
        )
        .andExpect(status().isUnauthorized());
    }
}
