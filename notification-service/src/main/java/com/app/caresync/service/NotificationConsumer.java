package com.app.caresync.service;

import com.app.caresync.config.RabbitMQConfig;
import com.app.caresync.dto.NotificationEvent;
import com.app.caresync.dto.NotificationRequest;
import com.app.caresync.model.NotificationChannel;
import com.app.caresync.model.NotificationType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeMessage(NotificationEvent event) {
        System.out.println("📥 [RabbitMQ] Received Notification for Recipient ID: " + event.getRecipientId());
        
        NotificationRequest request = new NotificationRequest();
        request.setRecipientId(event.getRecipientId());
        request.setRecipientRole(event.getRecipientRole());
        request.setTitle(event.getTitle());
        request.setMessage(event.getMessage());
        request.setType(NotificationType.valueOf(event.getType()));
        request.setChannel(NotificationChannel.valueOf(event.getChannel()));
        request.setRelatedId(event.getRelatedId());
        request.setRelatedType(event.getRelatedType());
        
        notificationService.send(request);
    }
}
