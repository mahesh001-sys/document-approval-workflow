package com.mahesh.daw.repository;

import com.mahesh.daw.entity.Notification;
import com.mahesh.daw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findByUserAndReadStatusFalseOrderByCreatedAtDesc(User user);

    long countByUserAndReadStatusFalse(User user);
}
