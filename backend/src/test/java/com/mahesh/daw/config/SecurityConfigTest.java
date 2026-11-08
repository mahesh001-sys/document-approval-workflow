package com.mahesh.daw.config;

import com.mahesh.daw.controller.RoleController;
import com.mahesh.daw.controller.UserController;
import com.mahesh.daw.dto.UserRegistrationRequest;
import com.mahesh.daw.entity.Role;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.security.JwtService;
import com.mahesh.daw.security.RestAccessDeniedHandler;
import com.mahesh.daw.security.RestAuthenticationEntryPoint;
import com.mahesh.daw.service.RoleService;
import com.mahesh.daw.service.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UserController.class,
        RoleController.class
})
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RoleService roleService;

    // ---------------------------------------------------------
    // Anonymous access
    // ---------------------------------------------------------

    @Test
    void anonymousUserCannotAccessUserManagement() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousUserCannotAccessRoleManagement() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousUserCannotAccessRequestQueue() throws Exception {
        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousUserCannotAccessManagerWorkflow() throws Exception {
        mockMvc.perform(post("/api/workflow/1/manager/review"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousUserCannotAccessAdminWorkflow() throws Exception {
        mockMvc.perform(post("/api/workflow/1/admin/review"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------------------------------------------------
    // Public registration
    // ---------------------------------------------------------

    @Test
    void anonymousUserCanRegister() throws Exception {
        Role userRole = Role.builder()
                .id(1L)
                .name("USER")
                .build();

        User registeredUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role(userRole)
                .active(true)
                .build();

        when(userService.createUser(any(UserRegistrationRequest.class)))
                .thenReturn(registeredUser);

        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Test",
                                  "lastName": "User",
                                  "email": "test@example.com",
                                  "password": "StrongPass123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    // ---------------------------------------------------------
    // User and role administration
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "USER")
    void normalUserCannotAccessUserManagement() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void normalUserCannotAccessRoleManagement() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCannotAccessUserManagement() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessUserManagement() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessRoleManagement() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk());
    }

    // ---------------------------------------------------------
    // Request queue permissions
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "USER")
    void employeeCannotAccessAllRequestsQueue() throws Exception {
        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanPassRequestQueueAuthorization() throws Exception {
        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanPassRequestQueueAuthorization() throws Exception {
        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // Employee request creation permissions
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "USER")
    void employeeCanPassRequestCreationAuthorization() throws Exception {
        mockMvc.perform(post("/api/requests"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCannotCreateEmployeeRequest() throws Exception {
        mockMvc.perform(post("/api/requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCannotCreateEmployeeRequest() throws Exception {
        mockMvc.perform(post("/api/requests"))
                .andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------
    // Manager workflow permissions
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "USER")
    void employeeCannotStartManagerReview() throws Exception {
        mockMvc.perform(post("/api/workflow/1/manager/review"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCannotPerformManagerWorkflowAction() throws Exception {
        mockMvc.perform(post("/api/workflow/1/manager/review"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanPassManagerWorkflowAuthorization() throws Exception {
        mockMvc.perform(post("/api/workflow/1/manager/review"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // Admin workflow permissions
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "USER")
    void employeeCannotStartAdminReview() throws Exception {
        mockMvc.perform(post("/api/workflow/1/admin/review"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCannotPerformAdminWorkflowAction() throws Exception {
        mockMvc.perform(post("/api/workflow/1/admin/review"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanPassAdminWorkflowAuthorization() throws Exception {
        mockMvc.perform(post("/api/workflow/1/admin/review"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // Disabled APIs
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void documentsApiRemainsDisabled() throws Exception {
        mockMvc.perform(get("/api/documents/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void commentsApiRemainsDisabled() throws Exception {
        mockMvc.perform(post("/api/comments"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void notificationsApiRemainsDisabled() throws Exception {
        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------
    // Approval history and audit log permissions
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "USER")
    void employeeCanPassApprovalHistoryAuthorization() throws Exception {
        mockMvc.perform(get("/api/approval-history/request/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanPassApprovalHistoryAuthorization() throws Exception {
        mockMvc.perform(get("/api/approval-history/request/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCannotReadAuditLogs() throws Exception {
        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanPassAuditLogAuthorization() throws Exception {
        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isNotFound());
    }
}
