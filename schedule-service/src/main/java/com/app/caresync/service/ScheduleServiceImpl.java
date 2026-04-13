package com.app.caresync.service;

import com.app.caresync.model.AvailabilitySlot;
import com.app.caresync.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private SlotRepository slotRepository;

    @Override
    public AvailabilitySlot addSlot(AvailabilitySlot slot) {
        return slotRepository.save(slot);
    }

    @Override
    public List<AvailabilitySlot> addBulkSlots(List<AvailabilitySlot> slots) {
        return slotRepository.saveAll(slots);
    }

    @Override
    public List<AvailabilitySlot> getSlotsByProvider(Long providerId) {
        return slotRepository.findByProviderId(providerId);
    }

    @Override
    @org.springframework.cache.annotation.Cacheable(value = "slots", key = "#providerId + '-' + #date")
    public List<AvailabilitySlot> getAvailableSlots(Long providerId, LocalDate date) {
        return slotRepository.findAvailableByProviderAndDate(providerId, date);
    }

    @Override
    public Optional<AvailabilitySlot> getSlotById(Long slotId) {
        return slotRepository.findById(slotId);
    }

    @Override
    public void bookSlot(Long slotId) {
        slotRepository.findById(slotId).ifPresent(s -> {
            s.setIsBooked(true);
            slotRepository.save(s);
        });
    }

    @Override
    public void unblockSlot(Long slotId) {
        slotRepository.findById(slotId).ifPresent(s -> {
            s.setIsBlocked(false);
            slotRepository.save(s);
        });
    }

    @Override
    public void deleteSlot(Long slotId) {
        slotRepository.deleteById(slotId);
    }

    @Override
    public AvailabilitySlot updateSlot(Long slotId, AvailabilitySlot slot) {
        slot.setSlotId(slotId);
        return slotRepository.save(slot);
    }

    @Override
    public void blockSlot(Long slotId) {
        slotRepository.findById(slotId).ifPresent(s -> {
            s.setIsBlocked(true);
            slotRepository.save(s);
        });
    }

    @Override
    public List<AvailabilitySlot> generateRecurringSlots(Long providerId, String pattern, LocalDate startDate, LocalDate endDate) {
        // Implementation for Section 2.5: Recurring slot generation
        return null;
    }

    // 🧹 Section 2.5: Automatically purge expired slots (past date/time with no booking)
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void purgeExpiredSlots() {
        // Logic to delete slots where date < current_date OR (date == current_date AND endTime < current_time) AND isBooked = false
    }
}
