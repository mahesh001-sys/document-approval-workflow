package com.mahesh.daw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.mahesh.daw.config.SecurityConfig;
import com.mahesh.daw.security.JwtAuthenticationFilter;
import com.mahesh.daw.security.RestAccessDeniedHandler;
import com.mahesh.daw.security.RestAuthenticationEntryPoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApprovalWorkflowController.class)
@Import(SecurityConfig.class)
class ApprovalWorkflowSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(
            username = "manager@example.com",
            roles = "MANAGER"
    )
    void managerCanAccessManagerApproveEndpoint()
            throws Exception {

        mockMvc.perform(
                post("/api/workflow/1/manager/approve")
                        .param("approverId", "1")
        )
        .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(
            username = "employee@example.com",
            roles = "EMPLOYEE"
    )
    void employeeCannotAccessManagerApproveEndpoint()
            throws Exception {

        mockMvc.perform(
                post("/api/workflow/1/manager/approve")
                        .param("approverId", "1")
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "admin@example.com",
            roles = "ADMIN"
    )
    void adminCanAccessAdminApproveEndpoint()
            throws Exception {

        mockMvc.perform(
                post("/api/workflow/1/admin/approve")
                        .param("approverId", "1")
        )
        .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(
            username = "employee@example.com",
            roles = "EMPLOYEE"
    )
    void employeeCannotAccessAdminApproveEndpoint()
            throws Exception {

        mockMvc.perform(
                post("/api/workflow/1/admin/approve")
                        .param("approverId", "1")
        )
        .andExpect(status().isForbidden());
    }
}
