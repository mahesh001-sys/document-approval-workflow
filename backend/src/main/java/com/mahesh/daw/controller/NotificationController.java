package com.mahesh.daw.controller;

import com.mahesh.daw.entity.Notification;
import com.mahesh.daw.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Create notification
    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @Valid @RequestBody Notification notification) {

        return ResponseEntity.ok(
                notificationService.createNotification(notification)
        );
    }

    // Get notification by ID
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                notificationService.getNotificationById(id)
        );
    }

    // Mark notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                notificationService.markAsRead(id)
        );
    }
}
