package com.delivery.notification;

import com.delivery.notification.config.RabbitMQConfig;
import com.delivery.notification.event.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        String message = "Order placed with id: " + event.getOrderId();
        notificationService.createNotification(event.getCustomerUsername(), message, NotificationType.ORDER_PLACED, event.getOrderId());
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleCourierAssigned(CourierAssignedEvent event) {
        String message = "Order " + event.getOrderId() + " assigned to you.";
        notificationService.createNotification(event.getCourierUsername(), message, NotificationType.COURIER_ASSIGNED, event.getOrderId());
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        String message = "Order " + event.getOrderId() + " status changed from " + event.getOldStatus() + " to " + event.getNewStatus();
        notificationService.createNotification(event.getCustomerUsername(), message, NotificationType.STATUS_CHANGED, event.getOrderId());
        if (event.getCourierUsername() != null) {
            notificationService.createNotification(event.getCourierUsername(), message, NotificationType.STATUS_CHANGED, event.getOrderId());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        String message = "Order " + event.getOrderId() + " cancelled.";
        notificationService.createNotification(event.getCustomerUsername(), message, NotificationType.STATUS_CHANGED, event.getOrderId());
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleSettlementRequested(SettlementRequestedEvent event) {
        // We'll skip for now, but we can later create an admin notification.
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleSettlementApproved(SettlementApprovedEvent event) {
        String message = "Settlement " + event.getSettlementId() + " approved.";
        notificationService.createNotification(event.getCourierUsername(), message, NotificationType.SETTLEMENT_APPROVED, event.getSettlementId());
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleSettlementRejected(SettlementRejectedEvent event) {
        String message = "Settlement " + event.getSettlementId() + " rejected: " + event.getReason();
        notificationService.createNotification(event.getCourierUsername(), message, NotificationType.SETTLEMENT_REJECTED, event.getSettlementId());
    }
}

