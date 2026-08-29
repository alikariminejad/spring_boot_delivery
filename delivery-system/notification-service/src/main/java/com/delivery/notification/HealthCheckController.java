package com.delivery.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications/test")
public class HealthCheckController {
    @GetMapping
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Notification service is up");
    }
}
