package org.example.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.notification.controller.dto.NotificationResponseDto;
import org.example.domain.notification.service.NotificationServiceImpl;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationServiceImpl notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(notificationService.getNotifications(userId, page, size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDto> markAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(notificationService.markAsRead(userId, id));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
