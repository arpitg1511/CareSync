package com.app.caresync.scheduler;

import com.app.caresync.model.Appointment;
import com.app.caresync.model.AppointmentStatus;
import com.app.caresync.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PDF Requirement §3.2 Use Case "Auto-Cancel No-Show":
 * "Flag appointments as No-Show if not completed within a time window."
 *
 * PDF Requirement (System actor):
 * "Automated component handling notifications, slot expiry, no-show detection, and follow-up reminders."
 */
@Component
public class NoShowDetectionScheduler {

    private static final Logger log = LoggerFactory.getLogger(NoShowDetectionScheduler.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Runs every 15 minutes.
     * Marks SCHEDULED appointments as NO_SHOW if their appointmentDateTime has passed
     * by more than 1 hour and they have not been marked COMPLETED.
     */
    @Scheduled(fixedDelay = 900_000) // every 15 minutes
    @Transactional
    public void detectNoShows() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        List<Appointment> overdue = appointmentRepository
                .findScheduledAppointmentsBeforeDateTime(cutoff);

        if (!overdue.isEmpty()) {
            overdue.forEach(a -> {
                a.setStatus(AppointmentStatus.NO_SHOW);
                a.setUpdatedAt(LocalDateTime.now());
            });
            appointmentRepository.saveAll(overdue);
            log.info("NoShowDetectionScheduler: flagged {} appointments as NO_SHOW.", overdue.size());
        }
    }
}
