package com.app.caresync.service;

import com.app.caresync.dto.NotificationRequest;
import com.app.caresync.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse send(NotificationRequest request);
    List<NotificationResponse> sendBulk(List<NotificationRequest> requests);
    void sendBookingConfirmation(Long patientId, Long providerId, Long appointmentId);
    void sendCancellationAlert(Long patientId, Long providerId, Long appointmentId);
    void sendPaymentReceipt(Long patientId, Long appointmentId, String amount);
    void sendAppointmentReminder(Long patientId, Long providerId, Long appointmentId, String timeLabel);
    void sendFollowUpReminder(Long patientId, Long recordId);
    List<NotificationResponse> getByRecipient(Long recipientId);
    List<NotificationResponse> getUnread(Long recipientId);
    long getUnreadCount(Long recipientId);
    NotificationResponse markAsRead(Long notificationId);
    void markAllRead(Long recipientId);
    void delete(Long notificationId);
    List<NotificationResponse> getAll();
    void sendEmail(Long recipientId, String email, String subject, String body);
    void sendSMS(Long recipientId, String phone, String message);
}
