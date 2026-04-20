package com.app.caresync.controller;

import com.app.caresync.dto.NotificationRequest;
import com.app.caresync.dto.NotificationResponse;
import com.app.caresync.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // PDF: getByRecipient
    @GetMapping("/recipient/{recipientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<NotificationResponse>> getByRecipient(@PathVariable Long recipientId) {
        return ResponseEntity.ok(notificationService.getByRecipient(recipientId));
    }

    // PDF: getUnread
    @GetMapping("/recipient/{recipientId}/unread")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<NotificationResponse>> getUnread(@PathVariable Long recipientId) {
        return ResponseEntity.ok(notificationService.getUnread(recipientId));
    }

    // PDF: getUnreadCount (for badge)
    @GetMapping("/recipient/{recipientId}/unread/count")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long recipientId) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(recipientId)));
    }

    // PDF: markAsRead
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId));
    }

    // PDF: markAllRead
    @PutMapping("/recipient/{recipientId}/read-all")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> markAllRead(@PathVariable Long recipientId) {
        notificationService.markAllRead(recipientId);
        return ResponseEntity.ok().build();
    }

    // PDF: delete
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long notificationId) {
        notificationService.delete(notificationId);
        return ResponseEntity.noContent().build();
    }

    // PDF: sendBulk (Admin broadcast)
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponse>> sendBulk(@RequestBody List<NotificationRequest> requests) {
        return ResponseEntity.ok(notificationService.sendBulk(requests));
    }

    // PDF: send single (admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> send(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.send(request));
    }

    // PDF: getAll (admin)
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponse>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    // Internal endpoints (called by other services without auth)
    @PostMapping("/internal/booking-confirmation")
    public ResponseEntity<Void> bookingConfirmation(@RequestParam Long patientId,
            @RequestParam Long providerId, @RequestParam Long appointmentId) {
        notificationService.sendBookingConfirmation(patientId, providerId, appointmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/cancellation-alert")
    public ResponseEntity<Void> cancellationAlert(@RequestParam Long patientId,
            @RequestParam Long providerId, @RequestParam Long appointmentId) {
        notificationService.sendCancellationAlert(patientId, providerId, appointmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/payment-receipt")
    public ResponseEntity<Void> paymentReceipt(@RequestParam Long patientId,
            @RequestParam Long appointmentId, @RequestParam String amount) {
        notificationService.sendPaymentReceipt(patientId, appointmentId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/reminder")
    public ResponseEntity<Void> reminder(@RequestParam Long patientId, @RequestParam Long providerId,
            @RequestParam Long appointmentId, @RequestParam String timeLabel) {
        notificationService.sendAppointmentReminder(patientId, providerId, appointmentId, timeLabel);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/followup-reminder")
    public ResponseEntity<Void> followUpReminder(@RequestParam Long patientId, @RequestParam Long recordId) {
        notificationService.sendFollowUpReminder(patientId, recordId);
        return ResponseEntity.ok().build();
    }
}
