package com.delivery.notification.controller;

import com.delivery.notification.NotificationService;
import com.delivery.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController{

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestHeader("X-User-Username") String username,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<NotificationResponse> notifications = notificationService.getNotifications(username, pageable);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id,
                                                           @RequestHeader("X-User-Username") String username){
        NotificationResponse notificationResponse = notificationService.markAsRead(id, username);
        return ResponseEntity.ok(notificationResponse);
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(@RequestHeader("X-User-Username") String username){
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok("All notifications are read");
    }
}