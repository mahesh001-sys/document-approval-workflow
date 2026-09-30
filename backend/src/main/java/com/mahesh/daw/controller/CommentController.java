package com.mahesh.daw.controller;

import com.mahesh.daw.entity.Comment;
import com.mahesh.daw.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Add a comment
    @PostMapping
    public ResponseEntity<Comment> addComment(
            @RequestBody Comment comment) {

        Comment savedComment =
                commentService.addComment(comment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedComment);
    }

    // Get comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                commentService.getCommentById(id)
        );
    }

    // Delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id) {

        commentService.deleteComment(id);

        return ResponseEntity.noContent().build();
    }
}
