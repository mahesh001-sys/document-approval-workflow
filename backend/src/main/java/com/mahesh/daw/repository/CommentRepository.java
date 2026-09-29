package com.mahesh.daw.repository;

import com.mahesh.daw.entity.Comment;
import com.mahesh.daw.entity.Request;
import com.mahesh.daw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByRequestOrderByCreatedAtAsc(Request request);

    List<Comment> findByUserOrderByCreatedAtDesc(User user);
}
