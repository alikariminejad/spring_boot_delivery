package com.delivery.notification;

import com.delivery.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(length=500, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(length=30, nullable = false)
    private NotificationType type;

    private UUID referenceId;

    private boolean isRead = false;

    private LocalDateTime createdAt;
}
