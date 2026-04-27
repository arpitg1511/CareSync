package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private String recipientRole; // PATIENT, DOCTOR, ADMIN

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String message;

    // PDF: type (BOOKING/REMINDER/CANCELLATION/PAYMENT/FOLLOWUP)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationType type = NotificationType.SYSTEM;

    // PDF: channel (APP/EMAIL/SMS)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationChannel channel = NotificationChannel.APP;

    // PDF: relatedId and relatedType for deep-linking
    private Long relatedId;
    private String relatedType; // APPOINTMENT, PAYMENT, REVIEW, RECORD

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    // PDF: sentAt field
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();
}
