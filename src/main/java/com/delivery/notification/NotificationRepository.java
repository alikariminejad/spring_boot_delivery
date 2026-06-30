package com.delivery.notification;

import com.delivery.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByRecipient(User recipient, Pageable pageable);
    int countByRecipientAndIsReadFalse(User recipient);

    @Modifying
    @Query("UPDATE Notification n  SET n.isRead = true WHERE n.recipient = :recipient AND n.isRead = false")
    @Transactional
    int markAllAsReadByRecipient(User recipient);
}
