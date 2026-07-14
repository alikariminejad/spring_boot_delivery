package com.delivery.notification;

import com.delivery.dto.NotificationResponse;
import com.delivery.mapper.NotificationMapper;
import com.delivery.user.User;
import com.delivery.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationResponse createNotification(String recipientUsername, String message, NotificationType type, UUID referenceId){
        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient not found"));

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        Notification savedNotif = notificationRepository.save(notification);
        return notificationMapper.toDto(savedNotif);
    }

    public Page<NotificationResponse> getNotifications(String username, Pageable pageable){
        User recipient = userRepository.findByUsername(username)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient not found"));
        Page<Notification> notifications = notificationRepository.findByRecipient(recipient, pageable);
        return notifications.map(notificationMapper::toDto);
    }

    @Transactional
    public void markAsRead(UUID notificationId, String username){
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        if(!notification.getRecipient().getUsername().equals(username)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Notification can not marked read by this user");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String username){
        User recipient = userRepository.findByUsername(username)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient not found"));
        notificationRepository.markAllAsReadByRecipient(recipient);
    }

}
