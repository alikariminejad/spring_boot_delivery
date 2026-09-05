package com.delivery.notification;

import com.delivery.notification.entity.Notification;
import com.delivery.notification.repository.NotificationRepository;
import com.delivery.notification.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class NotificationService{
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository){
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponse  createNotification(String recipientUsername, String message, String type, UUID referenceId){
        Notification notification = new Notification();
        notification.setRecipientUsername(recipientUsername);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setRead(false);          // explicit
        notificationRepository.save(notification);
        return notificationResponseMapper(notification);
    }

    public Page<NotificationResponse> getNotifications(String username, Pageable pageable){
        Page<Notification> notifications = notificationRepository.findByRecipientUsername(username, pageable);
        return notifications.map(this::notificationResponseMapper);
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId, String username){
        Notification notification = notificationRepository.findByIdAndRecipientUsername(notificationId, username)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found any notification"));
        notification.setRead(true);
        notificationRepository.save(notification);
        return notificationResponseMapper(notification);
    }
    @Transactional
    public void markAllAsRead(String username){
        notificationRepository.markAllAsReadByRecipient(username);
    }

    private NotificationResponse notificationResponseMapper(Notification notification){
        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setId(notification.getId());
        notificationResponse.setMessage(notification.getMessage());
        notificationResponse.setType(notification.getType());
        notificationResponse.setReferenceId(notification.getReferenceId());
        notificationResponse.setRead(notification.getRead());
        notificationResponse.setCreatedAt(notification.getCreatedAt());
        return notificationResponse;
    }
}