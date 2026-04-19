package com.app.caresync.service;

import com.app.caresync.dto.SlotRequest;
import com.app.caresync.dto.SlotResponse;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    SlotResponse addSlot(SlotRequest request);
    List<SlotResponse> addBulkSlots(List<SlotRequest> requests);
    List<SlotResponse> generateRecurringSlots(SlotRequest request);
    List<SlotResponse> getSlotsByProvider(Long providerId);
    List<SlotResponse> getAvailableSlotsByProviderAndDate(Long providerId, LocalDate date);
    List<SlotResponse> getUpcomingAvailableSlots(Long providerId);
    SlotResponse getSlotById(Long slotId);
    SlotResponse updateSlot(Long slotId, SlotRequest request);
    SlotResponse bookSlot(Long slotId);
    SlotResponse releaseSlot(Long slotId);
    SlotResponse blockSlot(Long slotId);
    SlotResponse unblockSlot(Long slotId);
    void deleteSlot(Long slotId);
    void deleteSlotsByProviderAndDate(Long providerId, java.time.LocalDate date);
}
