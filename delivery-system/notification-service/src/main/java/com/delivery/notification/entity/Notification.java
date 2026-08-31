package com.delivery.notification.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "recipient_username", length = 50, nullable = false)
    private String recipientUsername;
    @NotNull @Size(max = 500)
    private String message;
    @NotNull @Size(max = 30)
    private String type;
    private UUID referenceId;
    @Column(name = "is_read", nullable = false)
    private Boolean read = false;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
