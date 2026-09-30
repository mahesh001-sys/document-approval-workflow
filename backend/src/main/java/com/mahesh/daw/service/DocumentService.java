package com.mahesh.daw.service;

import com.mahesh.daw.entity.Document;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    public Document getDocumentById(Long id) {

        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Document not found with id: " + id
                        )
                );
    }

    public List<Document> getDocumentsByRequest(Request request) {
        return documentRepository.findByRequest(request);
    }

    public List<Document> getDocumentsByUser(User user) {
        return documentRepository.findByUploadedBy(user);
    }

    public List<Document> getDocumentsByRequestAndUser(
            Request request,
            User user) {

        return documentRepository.findByRequestAndUploadedBy(
                request,
                user
        );
    }

    public void deleteDocument(Long id) {

        Document document = getDocumentById(id);

        documentRepository.delete(document);
    }
}
