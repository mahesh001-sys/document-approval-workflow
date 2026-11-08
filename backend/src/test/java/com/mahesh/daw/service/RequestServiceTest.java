package com.mahesh.daw.service;

import com.mahesh.daw.dto.RequestCreateRequest;
import com.mahesh.daw.dto.RequestResponse;
import com.mahesh.daw.dto.RequestUpdateRequest;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.RequestPriority;
import com.mahesh.daw.entity.RequestStatus;
import com.mahesh.daw.entity.RequestType;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.exception.ForbiddenOperationException;
import com.mahesh.daw.exception.WorkflowConflictException;
import com.mahesh.daw.repository.RequestRepository;
import com.mahesh.daw.repository.RequestTypeRepository;
import com.mahesh.daw.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestTypeRepository requestTypeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RequestService requestService;

    @Test
    void createRequestShouldAssignRequesterAndGenerateRequestNumber() {

        User requester = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("user@example.com")
                .active(true)
                .build();

        RequestType requestType = RequestType.builder()
                .id(5L)
                .name("Leave Request")
                .active(true)
                .build();

        RequestCreateRequest input = new RequestCreateRequest(
                "Annual Leave",
                "Requesting leave for personal reasons",
                5L
        );

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(requester));

        when(requestTypeRepository.findById(5L))
                .thenReturn(Optional.of(requestType));

        when(requestRepository.save(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RequestResponse result = requestService.createRequest(
                input,
                "user@example.com"
        );

        assertNotNull(result.requestNumber());
        assertTrue(result.requestNumber().startsWith("REQ-"));
        assertEquals("Annual Leave", result.title());
        assertEquals(
                "Requesting leave for personal reasons",
                result.description()
        );
        assertEquals("DRAFT", result.status());
        assertEquals("MEDIUM", result.priority());
        assertEquals(5L, result.requestTypeId());
        assertEquals("Leave Request", result.requestTypeName());
        assertEquals(1L, result.requesterId());
        assertEquals("Test", result.requesterFirstName());
        assertEquals("User", result.requesterLastName());
        assertEquals("user@example.com", result.requesterEmail());

        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void createRequestShouldRejectInactiveRequestType() {

        User requester = User.builder()
                .id(1L)
                .email("user@example.com")
                .active(true)
                .build();

        RequestType requestType = RequestType.builder()
                .id(5L)
                .name("Old Request Type")
                .active(false)
                .build();

        RequestCreateRequest input = new RequestCreateRequest(
                "Test Request",
                "Test description",
                5L
        );

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(requester));

        when(requestTypeRepository.findById(5L))
                .thenReturn(Optional.of(requestType));

        assertThrows(
                ForbiddenOperationException.class,
                () -> requestService.createRequest(input, "user@example.com")
        );

        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void getRequestByIdForEmailShouldRejectAnotherEmployee() {

        User owner = User.builder()
                .id(10L)
                .email("owner@example.com")
                .build();

        Request request = Request.builder()
                .id(1L)
                .requester(owner)
                .status(RequestStatus.DRAFT)
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        assertThrows(
                ForbiddenOperationException.class,
                () -> requestService.getRequestResponseByIdForEmail(
                        1L,
                        "other@example.com"
                )
        );
    }

    @Test
    void submitRequestForUserShouldRejectAnotherEmployee() {

        User owner = User.builder()
                .id(10L)
                .email("owner@example.com")
                .build();

        Request request = Request.builder()
                .id(1L)
                .requester(owner)
                .status(RequestStatus.DRAFT)
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        assertThrows(
                ForbiddenOperationException.class,
                () -> requestService.submitRequestForUser(
                        1L,
                        "other@example.com"
                )
        );

        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void updateRequestForUserShouldRejectAnotherEmployee() {

        User owner = User.builder()
                .id(10L)
                .email("owner@example.com")
                .build();

        Request existingRequest = Request.builder()
                .id(1L)
                .requester(owner)
                .status(RequestStatus.DRAFT)
                .title("Original title")
                .build();

        RequestUpdateRequest updatedRequest = new RequestUpdateRequest(
                "Changed title",
                "Updated description",
                null,
                5L
        );

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(existingRequest));

        assertThrows(
                ForbiddenOperationException.class,
                () -> requestService.updateRequestForUser(
                        1L,
                        updatedRequest,
                        "other@example.com"
                )
        );

        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void deleteRequestForUserShouldRejectAnotherEmployee() {

        User owner = User.builder()
                .id(10L)
                .email("owner@example.com")
                .build();

        Request request = Request.builder()
                .id(1L)
                .requester(owner)
                .status(RequestStatus.DRAFT)
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        assertThrows(
                ForbiddenOperationException.class,
                () -> requestService.deleteRequestForUser(
                        1L,
                        "other@example.com"
                )
        );

        verify(requestRepository, never()).delete(any(Request.class));
    }


    @Test
    void getMyRequestsShouldReturnOnlyAuthenticatedEmployeesRequests() {

        User owner = User.builder()
                .id(10L)
                .email("owner@example.com")
                .build();

        Request ownedRequest = Request.builder()
                .id(1L)
                .requestNumber("REQ-OWNER-001")
                .title("Owner request")
                .status(RequestStatus.DRAFT)
                .priority(RequestPriority.MEDIUM)
                .requestType(RequestType.builder()
                        .id(5L)
                        .name("Leave Request")
                        .active(true)
                        .build())
                .requester(owner)
                .build();

        when(requestRepository.findByRequester_Email("owner@example.com"))
                .thenReturn(java.util.List.of(ownedRequest));

        java.util.List<RequestResponse> results =
                requestService.getMyRequests("owner@example.com");

        assertEquals(1, results.size());
        assertEquals("REQ-OWNER-001", results.get(0).requestNumber());
        verify(requestRepository)
                .findByRequester_Email("owner@example.com");
        verify(requestRepository, never()).findAll();
    }

    @Test
    void getRequestResponseByNumberForEmailShouldRejectAnotherEmployee() {

        User owner = User.builder()
                .id(10L)
                .email("owner@example.com")
                .build();

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-OWNER-001")
                .title("Owner request")
                .status(RequestStatus.DRAFT)
                .requester(owner)
                .build();

        when(requestRepository.findByRequestNumber("REQ-OWNER-001"))
                .thenReturn(Optional.of(request));

        assertThrows(
                ForbiddenOperationException.class,
                () -> requestService.getRequestResponseByNumberForEmail(
                        "REQ-OWNER-001",
                        "other@example.com"
                )
        );
    }

}
