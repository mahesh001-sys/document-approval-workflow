package com.mahesh.daw.controller;

import com.mahesh.daw.dto.ApprovalHistoryResponse;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.exception.ForbiddenOperationException;
import com.mahesh.daw.service.ApprovalHistoryService;
import com.mahesh.daw.service.RequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalHistoryControllerTest {

    @Mock
    private ApprovalHistoryService approvalHistoryService;

    @Mock
    private RequestService requestService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ApprovalHistoryController controller;

    @Test
    void employeeShouldAccessOwnRequestHistory() {

        Long requestId = 42L;
        String email = "employee@example.com";

        Request request = Request.builder()
                .id(requestId)
                .build();

        List<ApprovalHistoryResponse> history = List.of();

        doReturn(List.<GrantedAuthority>of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn(email);
        when(requestService.getRequestByIdForEmail(requestId, email))
                .thenReturn(request);
        when(approvalHistoryService.getHistoryByRequest(requestId))
                .thenReturn(history);

        ResponseEntity<List<ApprovalHistoryResponse>> response =
                controller.getHistoryByRequest(requestId, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertSame(history, response.getBody());

        verify(requestService).getRequestByIdForEmail(requestId, email);
        verify(approvalHistoryService).getHistoryByRequest(requestId);
        verify(requestService, never()).getRequestById(requestId);
    }

    @Test
    void employeeShouldNotAccessAnotherEmployeesHistory() {

        Long requestId = 42L;
        String email = "employee@example.com";

        doReturn(List.<GrantedAuthority>of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn(email);
        when(requestService.getRequestByIdForEmail(requestId, email))
                .thenThrow(new ForbiddenOperationException(
                        "You are not authorized to access this request"
                ));

        assertThrows(
                ForbiddenOperationException.class,
                () -> controller.getHistoryByRequest(requestId, authentication)
        );

        verify(requestService).getRequestByIdForEmail(requestId, email);
        verifyNoInteractions(approvalHistoryService);
    }

    @Test
    void managerShouldAccessAnotherEmployeesHistory() {

        Long requestId = 42L;

        Request request = Request.builder()
                .id(requestId)
                .build();

        List<ApprovalHistoryResponse> history = List.of();

        doReturn(List.<GrantedAuthority>of(new SimpleGrantedAuthority("ROLE_MANAGER"))).when(authentication).getAuthorities();
        when(requestService.getRequestById(requestId)).thenReturn(request);
        when(approvalHistoryService.getHistoryByRequest(requestId))
                .thenReturn(history);

        ResponseEntity<List<ApprovalHistoryResponse>> response =
                controller.getHistoryByRequest(requestId, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertSame(history, response.getBody());

        verify(requestService).getRequestById(requestId);
        verify(requestService, never())
                .getRequestByIdForEmail(anyLong(), anyString());
        verify(approvalHistoryService).getHistoryByRequest(requestId);
    }
}
