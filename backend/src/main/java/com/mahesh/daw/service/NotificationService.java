package com.mahesh.daw.service;

import com.mahesh.daw.entity.Notification;
import com.mahesh.daw.entity.User;
import com.mahesh.daw.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification getNotificationById(Long id) {

        return notificationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Notification not found with id: " + id
                        )
                );
    }

    public List<Notification> getNotificationsByUser(User user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotifications(User user) {

        return notificationRepository
                .findByUserAndReadStatusFalseOrderByCreatedAtDesc(user);
    }

    public long getUnreadNotificationCount(User user) {

        return notificationRepository
                .countByUserAndReadStatusFalse(user);
    }

    public Notification markAsRead(Long id) {

        Notification notification = getNotificationById(id);

        notification.setReadStatus(true);

        return notificationRepository.save(notification);
    }
}
