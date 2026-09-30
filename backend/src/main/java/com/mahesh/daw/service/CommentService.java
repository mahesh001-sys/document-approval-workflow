package com.mahesh.daw.service;

import com.mahesh.daw.entity.Comment;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment addComment(Comment comment) {

        if (comment.getContent() == null
                || comment.getContent().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Comment content cannot be empty"
            );
        }

        return commentRepository.save(comment);
    }

    public Comment getCommentById(Long id) {

        return commentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Comment not found with id: " + id
                        )
                );
    }

    public List<Comment> getCommentsByRequest(Request request) {

        return commentRepository
                .findByRequestOrderByCreatedAtAsc(request);
    }

    public List<Comment> getCommentsByUser(User user) {

        return commentRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    public void deleteComment(Long id) {

        Comment comment = getCommentById(id);

        commentRepository.delete(comment);
    }
}
