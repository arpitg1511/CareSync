package com.app.caresync.scheduler;

import com.app.caresync.model.MedicalRecord;
import com.app.caresync.repository.RecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

/**
 * PDF Requirement §2.7:
 * "Records with a follow-up date trigger an automated reminder notification to the patient on that date."
 *
 * PDF Use Case "Send Follow-up Reminder" (System actor):
 * "Notify patient on follow-up date set in the medical record."
 */
@Component
public class FollowUpReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(FollowUpReminderScheduler.class);

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${NOTIFICATION_SERVICE_URL:http://localhost:8089}")
    private String notificationServiceUrl;

    /**
     * Runs every day at 8 AM.
     * Finds all medical records where followUpDate == today and sends reminder notifications.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendFollowUpReminders() {
        LocalDate today = LocalDate.now();
        List<MedicalRecord> dueRecords = recordRepository.findByFollowUpDate(today);

        if (dueRecords.isEmpty()) return;

        log.info("FollowUpReminderScheduler: {} follow-up reminders to send.", dueRecords.size());

        for (MedicalRecord record : dueRecords) {
            try {
                String url = notificationServiceUrl +
                        "/api/notifications/internal/followup-reminder?patientId=" +
                        record.getPatientId() + "&recordId=" + record.getRecordId();
                restTemplate.postForEntity(url, null, Void.class);
                log.info("Follow-up reminder sent for record #{}", record.getRecordId());
            } catch (Exception e) {
                log.error("Failed to send follow-up reminder for record #{}: {}",
                        record.getRecordId(), e.getMessage());
            }
        }
    }
}
