package com.app.caresync.service;

import com.app.caresync.dto.NotificationRequest;
import com.app.caresync.dto.NotificationResponse;
import com.app.caresync.exception.NotificationNotFoundException;
import com.app.caresync.model.Notification;
import com.app.caresync.model.NotificationChannel;
import com.app.caresync.model.NotificationType;
import com.app.caresync.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public NotificationResponse send(NotificationRequest request) {
        Notification n = Notification.builder()
                .recipientId(request.getRecipientId())
                .recipientRole(request.getRecipientRole() != null ? request.getRecipientRole() : "PATIENT")
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType() != null ? request.getType() : NotificationType.SYSTEM)
                .channel(request.getChannel() != null ? request.getChannel() : NotificationChannel.APP)
                .relatedId(request.getRelatedId())
                .relatedType(request.getRelatedType())
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();
        return mapToResponse(notificationRepository.save(n));
    }

    @Override
    public List<NotificationResponse> sendBulk(List<NotificationRequest> requests) {
        return requests.stream().map(this::send).collect(Collectors.toList());
    }

    @Override
    public void sendBookingConfirmation(Long patientId, Long providerId, Long appointmentId) {
        NotificationRequest patientReq = new NotificationRequest();
        patientReq.setRecipientId(patientId); patientReq.setRecipientRole("PATIENT");
        patientReq.setTitle("Appointment Confirmed");
        patientReq.setMessage("Your appointment #" + appointmentId + " has been confirmed.");
        patientReq.setType(NotificationType.BOOKING); patientReq.setChannel(NotificationChannel.APP);
        patientReq.setRelatedId(appointmentId); patientReq.setRelatedType("APPOINTMENT");
        send(patientReq);

        NotificationRequest providerReq = new NotificationRequest();
        providerReq.setRecipientId(providerId); providerReq.setRecipientRole("DOCTOR");
        providerReq.setTitle("New Appointment Booked");
        providerReq.setMessage("New appointment #" + appointmentId + " has been booked.");
        providerReq.setType(NotificationType.BOOKING); providerReq.setChannel(NotificationChannel.APP);
        providerReq.setRelatedId(appointmentId); providerReq.setRelatedType("APPOINTMENT");
        send(providerReq);
    }

    @Override
    public void sendCancellationAlert(Long patientId, Long providerId, Long appointmentId) {
        NotificationRequest p = new NotificationRequest();
        p.setRecipientId(patientId); p.setRecipientRole("PATIENT");
        p.setTitle("Appointment Cancelled");
        p.setMessage("Appointment #" + appointmentId + " cancelled. Refund will be processed if eligible.");
        p.setType(NotificationType.CANCELLATION); p.setChannel(NotificationChannel.APP);
        p.setRelatedId(appointmentId); p.setRelatedType("APPOINTMENT");
        send(p);

        NotificationRequest d = new NotificationRequest();
        d.setRecipientId(providerId); d.setRecipientRole("DOCTOR");
        d.setTitle("Appointment Cancelled");
        d.setMessage("Appointment #" + appointmentId + " was cancelled by the patient.");
        d.setType(NotificationType.CANCELLATION); d.setChannel(NotificationChannel.APP);
        d.setRelatedId(appointmentId); d.setRelatedType("APPOINTMENT");
        send(d);
    }

    @Override
    public void sendPaymentReceipt(Long patientId, Long appointmentId, String amount) {
        NotificationRequest p = new NotificationRequest();
        p.setRecipientId(patientId); p.setRecipientRole("PATIENT");
        p.setTitle("Payment Confirmed");
        p.setMessage("Payment of ₹" + amount + " received for appointment #" + appointmentId + ".");
        p.setType(NotificationType.PAYMENT); p.setChannel(NotificationChannel.APP);
        p.setRelatedId(appointmentId); p.setRelatedType("PAYMENT");
        send(p);
    }

    @Override
    public void sendAppointmentReminder(Long patientId, Long providerId, Long appointmentId, String timeLabel) {
        NotificationRequest p = new NotificationRequest();
        p.setRecipientId(patientId); p.setRecipientRole("PATIENT");
        p.setTitle("Appointment Reminder");
        p.setMessage("Reminder: You have an appointment #" + appointmentId + " in " + timeLabel + ".");
        p.setType(NotificationType.REMINDER); p.setChannel(NotificationChannel.APP);
        p.setRelatedId(appointmentId); p.setRelatedType("APPOINTMENT");
        send(p);

        NotificationRequest d = new NotificationRequest();
        d.setRecipientId(providerId); d.setRecipientRole("DOCTOR");
        d.setTitle("Upcoming Appointment");
        d.setMessage("Appointment #" + appointmentId + " is scheduled in " + timeLabel + ".");
        d.setType(NotificationType.REMINDER); d.setChannel(NotificationChannel.APP);
        d.setRelatedId(appointmentId); d.setRelatedType("APPOINTMENT");
        send(d);
    }

    @Override
    public void sendFollowUpReminder(Long patientId, Long recordId) {
        NotificationRequest p = new NotificationRequest();
        p.setRecipientId(patientId); p.setRecipientRole("PATIENT");
        p.setTitle("Follow-Up Reminder");
        p.setMessage("Today is your scheduled follow-up date. Please contact your provider or book an appointment.");
        p.setType(NotificationType.FOLLOWUP); p.setChannel(NotificationChannel.APP);
        p.setRelatedId(recordId); p.setRelatedType("RECORD");
        send(p);
    }

    @Override
    public List<NotificationResponse> getByRecipient(Long recipientId) {
        return notificationRepository.findByRecipientIdOrderBySentAtDesc(recipientId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnread(Long recipientId) {
        return notificationRepository.findByRecipientIdAndIsRead(recipientId, false)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(Long recipientId) {
        return notificationRepository.countByRecipientIdAndIsRead(recipientId, false);
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + notificationId));
        n.setIsRead(true);
        return mapToResponse(notificationRepository.save(n));
    }

    @Override
    public void markAllRead(Long recipientId) {
        List<Notification> unread = notificationRepository.findByRecipientIdAndIsRead(recipientId, false);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    @Override
    public void delete(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new NotificationNotFoundException("Notification not found with id: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public List<NotificationResponse> getAll() {
        return notificationRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public void sendEmail(Long recipientId, String email, String subject, String body) {
        NotificationRequest req = new NotificationRequest();
        req.setRecipientId(recipientId);
        req.setRecipientRole("PATIENT");
        req.setTitle(subject);
        req.setMessage(body);
        req.setType(NotificationType.SYSTEM);
        req.setChannel(NotificationChannel.EMAIL);
        send(req);
    }

    @Override
    public void sendSMS(Long recipientId, String phone, String message) {
        NotificationRequest req = new NotificationRequest();
        req.setRecipientId(recipientId);
        req.setRecipientRole("PATIENT");
        req.setTitle("SMS Notification");
        req.setMessage(message);
        req.setType(NotificationType.SYSTEM);
        req.setChannel(NotificationChannel.SMS);
        send(req);
    }

    private NotificationResponse mapToResponse(Notification n) {
        if (n == null) return null;
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .recipientId(n.getRecipientId())
                .recipientRole(n.getRecipientRole())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .channel(n.getChannel())
                .relatedId(n.getRelatedId())
                .relatedType(n.getRelatedType())
                .isRead(n.getIsRead())
                .sentAt(n.getSentAt())
                .build();
    }
}
