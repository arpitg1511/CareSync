package com.app.caresync.scheduler;

import com.app.caresync.repository.SlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import com.app.caresync.model.AvailabilitySlot;

/**
 * PDF Requirement §2.5:
 * "Expired slots (past date/time with no booking) are automatically purged by a scheduled job."
 */
@Component
public class SlotExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(SlotExpiryScheduler.class);

    @Autowired
    private SlotRepository slotRepository;

    /**
     * Runs daily at midnight.
     * Deletes all unbooked, unblocked slots whose date is before today.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void purgeExpiredSlots() {
        LocalDate today = LocalDate.now();
        List<AvailabilitySlot> expired = slotRepository.findExpiredUnbookedSlots(today);
        if (!expired.isEmpty()) {
            slotRepository.deleteAll(expired);
            log.info("SlotExpiryScheduler: purged {} expired unbooked slots.", expired.size());
        }
    }
}
