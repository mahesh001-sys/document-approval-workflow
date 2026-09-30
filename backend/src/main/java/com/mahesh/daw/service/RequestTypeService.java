package com.mahesh.daw.service;

import com.mahesh.daw.entity.RequestType;
import com.mahesh.daw.repository.RequestTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestTypeService {

    private final RequestTypeRepository requestTypeRepository;

    public RequestType createRequestType(RequestType requestType) {

        if (requestTypeRepository.existsByName(requestType.getName())) {
            throw new IllegalArgumentException(
                    "Request type already exists: " + requestType.getName()
            );
        }

        return requestTypeRepository.save(requestType);
    }

    public RequestType getRequestTypeById(Long id) {

        return requestTypeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Request type not found with id: " + id
                        )
                );
    }

    public RequestType getRequestTypeByName(String name) {

        return requestTypeRepository.findByName(name)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Request type not found: " + name
                        )
                );
    }

    public List<RequestType> getAllRequestTypes() {
        return requestTypeRepository.findAll();
    }

    public List<RequestType> getActiveRequestTypes() {
        return requestTypeRepository.findByActiveTrue();
    }
}
