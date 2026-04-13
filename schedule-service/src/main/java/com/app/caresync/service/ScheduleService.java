package com.app.caresync.service;

import com.app.caresync.model.AvailabilitySlot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    AvailabilitySlot addSlot(AvailabilitySlot slot);
    List<AvailabilitySlot> addBulkSlots(List<AvailabilitySlot> slots);
    List<AvailabilitySlot> getSlotsByProvider(Long providerId);
    List<AvailabilitySlot> getAvailableSlots(Long providerId, LocalDate date);
    Optional<AvailabilitySlot> getSlotById(Long slotId);
    void bookSlot(Long slotId);
    void unblockSlot(Long slotId);
    void deleteSlot(Long slotId);
    AvailabilitySlot updateSlot(Long slotId, AvailabilitySlot slot);
    void blockSlot(Long slotId);
    List<AvailabilitySlot> generateRecurringSlots(Long providerId, String pattern, LocalDate startDate, LocalDate endDate);
}
