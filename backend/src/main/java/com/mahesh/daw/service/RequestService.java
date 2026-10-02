package com.mahesh.daw.service;

import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.RequestStatus;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public Request createRequest(Request request) {

        if (request.getRequestNumber() != null
                && requestRepository.existsByRequestNumber(
                request.getRequestNumber())) {

            throw new IllegalArgumentException(
                    "Request already exists with number: "
                            + request.getRequestNumber()
            );
        }

        if (request.getStatus() == null) {
            request.setStatus(RequestStatus.DRAFT);
        }

        return requestRepository.save(request);
    }

    public Request getRequestById(Long id) {

        return requestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Request not found with id: " + id
                        )
                );
    }

    public Request getRequestByIdForUser(
            Long id,
            User user) {

        Request request = getRequestById(id);

        validateOwnership(request, user);

        return request;
    }

    public Request getRequestByNumber(String requestNumber) {

        return requestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Request not found: "
                                        + requestNumber
                        )
                );
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public List<Request> getRequestsByRequester(User requester) {
        return requestRepository.findByRequester(requester);
    }

    public List<Request> getRequestsByStatus(
            RequestStatus status) {

        return requestRepository.findByStatus(status);
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
            throw new IllegalArgumentException(
                    "Only DRAFT requests can be submitted"
            );
        }

        request.setStatus(RequestStatus.SUBMITTED);
        request.setSubmittedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    public Request updateRequest(Request request) {
        return requestRepository.save(request);
    }

    public void deleteRequest(Long id) {

        Request request = getRequestById(id);

        requestRepository.delete(request);
    }

    private void validateOwnership(
            Request request,
            User user) {

        if (request.getRequester() == null
                || user == null
                || request.getRequester().getId() == null
                || user.getId() == null
                || !request.getRequester()
                        .getId()
                        .equals(user.getId())) {

            throw new SecurityException(
                    "You are not authorized to access this request"
            );
        }
    }
}
