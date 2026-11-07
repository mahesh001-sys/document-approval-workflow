package com.mahesh.daw.service;

import com.mahesh.daw.dto.RequestCreateRequest;
import com.mahesh.daw.dto.RequestResponse;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.RequestPriority;
import com.mahesh.daw.entity.RequestType;
import com.mahesh.daw.entity.RequestStatus;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.exception.ForbiddenOperationException;
import com.mahesh.daw.exception.ResourceNotFoundException;
import com.mahesh.daw.exception.WorkflowConflictException;
import com.mahesh.daw.repository.RequestRepository;
import com.mahesh.daw.repository.RequestTypeRepository;
import com.mahesh.daw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final UserRepository userRepository;

    @Transactional
    public RequestResponse createRequest(
            RequestCreateRequest request,
            String requesterEmail) {

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + requesterEmail
                        )
                );

        if (Boolean.FALSE.equals(requester.getActive())) {
            throw new ForbiddenOperationException(
                    "Inactive users cannot create requests"
            );
        }

        RequestType requestType =
                requestTypeRepository.findById(request.requestTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Request type not found with id: "
                                                + request.requestTypeId()
                                )
                        );

        if (!Boolean.TRUE.equals(requestType.getActive())) {
            throw new ForbiddenOperationException(
                    "This request type is inactive"
            );
        }

        Request newRequest = Request.builder()
                .requestNumber(
                        "REQ-" + UUID.randomUUID().toString().toUpperCase()
                )
                .title(request.title().trim())
                .description(request.description())
                .status(RequestStatus.DRAFT)
                .priority(RequestPriority.MEDIUM)
                .requester(requester)
                .requestType(requestType)
                .build();

        return RequestResponse.from(requestRepository.save(newRequest));
    }

    public Request getRequestById(Long id) {

        return requestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Request not found with id: " + id
                        )
                );
    }

    public Request getRequestByIdForUser(Long id, User user) {

        Request request = getRequestById(id);

        validateOwnership(request, user);

        return request;
    }

    @Transactional(readOnly = true)
    public Request getRequestByIdForEmail(Long id, String email) {

        Request request = getRequestById(id);

        if (request.getRequester() == null
                || request.getRequester().getEmail() == null
                || !request.getRequester()
                        .getEmail()
                        .equalsIgnoreCase(email)) {

            throw new ForbiddenOperationException(
                    "You are not authorized to access this request"
            );
        }

        return request;
    }

    @Transactional(readOnly = true)
    public RequestResponse getRequestResponseByIdForEmail(
            Long id,
            String email) {

        return RequestResponse.from(
                getRequestByIdForEmail(id, email)
        );
    }

    public Request getRequestByNumber(String requestNumber) {

        return requestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Request not found: " + requestNumber
                        )
                );
    }

    @Transactional(readOnly = true)
    public RequestResponse getRequestResponseByNumber(String requestNumber) {

        return RequestResponse.from(
                getRequestByNumber(requestNumber)
        );
    }

    @Transactional(readOnly = true)
    public List<RequestResponse> getAllRequests() {

        return requestRepository.findAll()
                .stream()
                .map(RequestResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RequestResponse> getMyRequests(String email) {

        return requestRepository.findByRequester_Email(email)
                .stream()
                .map(RequestResponse::from)
                .toList();
    }

    public List<Request> getRequestsByRequester(User requester) {
        return requestRepository.findByRequester(requester);
    }

    @Transactional(readOnly = true)
    public List<RequestResponse> getRequestsByStatus(
            RequestStatus status) {

        return requestRepository.findByStatus(status)
                .stream()
                .map(RequestResponse::from)
                .toList();
    }

    public List<Request> getRequestsByRequesterAndStatus(
            User requester,
            RequestStatus status) {

        return requestRepository.findByRequesterAndStatus(
                requester,
                status
        );
    }

    public Request submitRequest(Long id) {

        Request request = getRequestById(id);

        if (request.getStatus() != RequestStatus.DRAFT) {
            throw new WorkflowConflictException(
                    "Only DRAFT requests can be submitted"
            );
        }

        request.setStatus(RequestStatus.SUBMITTED);
        request.setSubmittedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Transactional
    public RequestResponse submitRequestForUser(Long id, String email) {

        Request request = getRequestByIdForEmail(id, email);

        if (request.getStatus() != RequestStatus.DRAFT) {
            throw new WorkflowConflictException(
                    "Only DRAFT requests can be submitted"
            );
        }

        request.setStatus(RequestStatus.SUBMITTED);
        request.setSubmittedAt(LocalDateTime.now());

        return RequestResponse.from(
                requestRepository.save(request)
        );
    }

    public Request updateRequest(Request request) {
        return requestRepository.save(request);
    }

    @Transactional
    public RequestResponse updateRequestForUser(
            Long id,
            Request updatedRequest,
            String email) {

        Request existingRequest = getRequestByIdForEmail(id, email);

        existingRequest.setTitle(updatedRequest.getTitle());
        existingRequest.setDescription(updatedRequest.getDescription());
        existingRequest.setPriority(updatedRequest.getPriority());
        existingRequest.setRequestType(updatedRequest.getRequestType());

        return RequestResponse.from(
                requestRepository.save(existingRequest)
        );
    }

    public void deleteRequest(Long id) {

        Request request = getRequestById(id);

        requestRepository.delete(request);
    }

    @Transactional
    public void deleteRequestForUser(Long id, String email) {

        Request request = getRequestByIdForEmail(id, email);

        requestRepository.delete(request);
    }

    private void validateOwnership(Request request, User user) {

        if (request.getRequester() == null
                || user == null
                || request.getRequester().getId() == null
                || user.getId() == null
                || !request.getRequester()
                        .getId()
                        .equals(user.getId())) {

            throw new ForbiddenOperationException(
                    "You are not authorized to access this request"
            );
        }
    }
}
