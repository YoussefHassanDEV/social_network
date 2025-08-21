package com.youssef.socialnetwork.controllers;


import com.youssef.socialnetwork.dto.NotificationResponseDTO;
import com.youssef.socialnetwork.model.Notification;
import com.youssef.socialnetwork.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private NotificationResponseDTO toDto(Notification n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .message(n.getMessage())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }

    @PostMapping("/{recipientId}")
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @PathVariable Long recipientId,
            @RequestBody String message) {
        return ResponseEntity.ok(toDto(notificationService.createNotification(recipientId, message)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(
                notificationService.getUserNotifications(userId)
                        .stream().map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/read/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
