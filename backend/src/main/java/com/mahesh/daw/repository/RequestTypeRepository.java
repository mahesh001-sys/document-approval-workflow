package com.mahesh.daw.repository;

import com.mahesh.daw.entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestTypeRepository extends JpaRepository<RequestType, Long> {

    Optional<RequestType> findByName(String name);

    boolean existsByName(String name);

    List<RequestType> findByActiveTrue();
}
