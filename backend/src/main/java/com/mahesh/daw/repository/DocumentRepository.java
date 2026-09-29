package com.mahesh.daw.repository;

import com.mahesh.daw.entity.Document;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByRequest(Request request);

    List<Document> findByUploadedBy(User uploadedBy);

    List<Document> findByRequestAndUploadedBy(
            Request request,
            User uploadedBy
    );
}
