package com.mahesh.daw.service;

import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.RequestStatus;
import com.mahesh.daw.repository.RequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    private RequestService requestService;

    @Test
    void submitDraftRequestShouldChangeStatusToSubmitted() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.DRAFT)
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(requestRepository.save(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result = requestService.submitRequest(1L);

        assertEquals(
                RequestStatus.SUBMITTED,
                result.getStatus()
        );

        assertNotNull(result.getSubmittedAt());

        verify(requestRepository).findById(1L);
        verify(requestRepository).save(request);
    }

    @Test
    void submitNonDraftRequestShouldThrowException() {

        Request request = Request.builder()
                .id(1L)
                .requestNumber("REQ-001")
                .status(RequestStatus.SUBMITTED)
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> requestService.submitRequest(1L)
                );

        assertEquals(
                "Only DRAFT requests can be submitted",
                exception.getMessage()
        );

        verify(requestRepository).findById(1L);
        verify(requestRepository, never())
                .save(any(Request.class));
    }
}
