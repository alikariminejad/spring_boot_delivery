package com.delivery.notification.repository;

import com.delivery.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByRecipientUsername(String recipientUsername, Pageable pageable);
    @Query("SELECT n FROM Notification n WHERE n.id = :id AND n.recipientUsername = :username")
    Optional<Notification> findByIdAndRecipientUsername(@Param("id") UUID id, @Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipientUsername = :username")
    int markAllAsReadByRecipient(@Param("username") String username);
}
