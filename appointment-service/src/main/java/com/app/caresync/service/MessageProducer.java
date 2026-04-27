package com.app.caresync.service;

import com.app.caresync.config.RabbitMQConfig;
import com.app.caresync.dto.NotificationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    @Autowired
    private RabbitTemplate template;

    public void sendNotification(NotificationEvent event) {
        template.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
        System.out.println("[RabbitMQ] Sent Notification Event for Patient ID: " + event.getRecipientId());
    }
}
