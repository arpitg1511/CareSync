package com.app.caresync.service;

import com.app.caresync.model.Notification;
import java.util.List;

public interface NotificationService {
    Notification sendNotification(Notification notification);
    List<Notification> getByRecipient(Long recipientId);
    List<Notification> getUnread(Long recipientId);
    long getUnreadCount(Long recipientId);
    void markAllAsRead(Long recipientId);
}
