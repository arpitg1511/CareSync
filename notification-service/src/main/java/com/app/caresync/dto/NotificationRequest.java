package com.app.caresync.dto;

import com.app.caresync.model.NotificationChannel;
import com.app.caresync.model.NotificationType;
import lombok.Data;

@Data
public class NotificationRequest {
    private Long recipientId;
    private String recipientRole;
    private String title;
    private String message;
    private NotificationType type;       // PDF: BOOKING/REMINDER/CANCELLATION/PAYMENT/FOLLOWUP
    private NotificationChannel channel; // PDF: APP/EMAIL/SMS
    private Long relatedId;
    private String relatedType;
}
