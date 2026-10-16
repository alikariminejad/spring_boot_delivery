package com.delivery.notification.controller;

import com.delivery.notification.NotificationService;
import com.delivery.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController{

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = getUsername();
        Page<NotificationResponse> notifications = notificationService.getNotifications(username, pageable);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id){
        String username = getUsername();
        NotificationResponse notificationResponse = notificationService.markAsRead(id, username);
        return ResponseEntity.ok(notificationResponse);
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(){
        String username = getUsername();
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok("All notifications are read");
    }

    private String getUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}