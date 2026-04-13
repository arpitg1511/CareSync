package com.app.caresync.repository;

import com.app.caresync.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientId(Long recipientId);
    List<Notification> findByRecipientIdAndIsRead(Long recipientId, Boolean isRead);
    long countByRecipientIdAndIsRead(Long recipientId, Boolean isRead);
    List<Notification> findByRelatedId(Long relatedId);
}
