package com.delivery.notification;

import com.delivery.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal UserDetails user,
                                                                         Pageable pageable){
        Page<NotificationResponse> notificationResponses = notificationService.getNotifications(user.getUsername(), pageable);
        return ResponseEntity.ok(notificationResponses);
    };

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable UUID id, @AuthenticationPrincipal UserDetails user){
        notificationService.markAsRead(id, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal UserDetails user){
        notificationService.markAllAsRead(user.getUsername());
        return ResponseEntity.ok().build();
    }
}
