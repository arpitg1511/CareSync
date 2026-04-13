package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long recipientId;

    private String type; // BOOKING, REMINDER, CANCELLATION, PAYMENT, FOLLOWUP

    private String title;

    private String message;

    private String channel; // APP, EMAIL, SMS

    private Long relatedId;

    private String relatedType;

    @Builder.Default
    private Boolean isRead = false;

    private LocalDateTime sentAt = LocalDateTime.now();
}
