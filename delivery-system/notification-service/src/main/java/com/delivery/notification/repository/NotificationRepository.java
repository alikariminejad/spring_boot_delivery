package com.delivery.notification.repository;

import com.delivery.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByRecipientUsername(String recipientUsername, Pageable pageable);
    Page<Notification> findByRecipientUsername(String recipientUsername);
    Notification findByIdAndRecipientUsername(UUID id,String recipientUsername);
}
