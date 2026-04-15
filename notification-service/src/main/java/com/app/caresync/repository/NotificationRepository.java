package com.app.caresync.repository;

import com.app.caresync.model.Notification;
import com.app.caresync.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // PDF: findByRecipientId()
    List<Notification> findByRecipientIdOrderBySentAtDesc(Long recipientId);

    // PDF: findByRecipientIdAndIsRead()
    List<Notification> findByRecipientIdAndIsRead(Long recipientId, Boolean isRead);

    // PDF: countByRecipientIdAndIsRead()
    long countByRecipientIdAndIsRead(Long recipientId, Boolean isRead);

    // PDF: findByType()
    List<Notification> findByType(NotificationType type);

    // PDF: findByRelatedId()
    List<Notification> findByRelatedId(Long relatedId);

    List<Notification> findByRecipientRole(String role);
}
