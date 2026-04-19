package com.app.caresync.dto;

import com.app.caresync.model.NotificationChannel;
import com.app.caresync.model.NotificationType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class NotificationResponse {
    private Long notificationId;
    private Long recipientId;
    private String recipientRole;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationChannel channel;
    private Long relatedId;
    private String relatedType;
    private Boolean isRead;
    private LocalDateTime sentAt;
}
