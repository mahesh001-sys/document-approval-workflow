package com.mahesh.daw.repository;

import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.RequestStatus;
import com.mahesh.daw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByRequestNumber(String requestNumber);

    boolean existsByRequestNumber(String requestNumber);

    List<Request> findByRequester(User requester);

    List<Request> findByStatus(RequestStatus status);

    List<Request> findByRequesterAndStatus(
            User requester,
            RequestStatus status
    );
}
