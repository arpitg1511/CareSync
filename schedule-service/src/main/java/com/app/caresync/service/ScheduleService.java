package com.app.caresync.service;

import com.app.caresync.dto.SlotRequest;
import com.app.caresync.dto.SlotResponse;
import com.app.caresync.model.AvailabilitySlot;
import com.app.caresync.model.Recurrence;
import com.app.caresync.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private SlotRepository slotRepository;

    // PDF: addSlot()
    public SlotResponse addSlot(SlotRequest request) {
        AvailabilitySlot slot = buildSlot(request);
        return mapToResponse(slotRepository.save(slot));
    }

    // PDF: addBulkSlots() — add a list of slots at once
    public List<SlotResponse> addBulkSlots(List<SlotRequest> requests) {
        List<AvailabilitySlot> slots = requests.stream()
                .map(this::buildSlot)
                .collect(Collectors.toList());
        return slotRepository.saveAll(slots).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: generateRecurringSlots()
    public List<SlotResponse> generateRecurringSlots(SlotRequest request) {
        List<AvailabilitySlot> slots = new ArrayList<>();
        LocalDate current = request.getDate();
        LocalDate end = request.getEndDate() != null ? request.getEndDate() : current.plusWeeks(4);
        String pattern = request.getRecurrencePattern() != null ? request.getRecurrencePattern().toUpperCase() : "DAILY";

        while (!current.isAfter(end)) {
            AvailabilitySlot slot = AvailabilitySlot.builder()
                    .providerId(request.getProviderId())
                    .date(current)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 30)
                    .recurrence("WEEKLY".equals(pattern) ? Recurrence.WEEKLY : Recurrence.DAILY)
                    .build();
            slots.add(slot);
            current = "WEEKLY".equals(pattern) ? current.plusWeeks(1) : current.plusDays(1);
        }
        return slotRepository.saveAll(slots).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: getSlotsByProvider()
    public List<SlotResponse> getSlotsByProvider(Long providerId) {
        return slotRepository.findByProviderId(providerId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: getAvailableSlots() — only unbooked, unblocked slots exposed to patients
    public List<SlotResponse> getAvailableSlotsByProviderAndDate(Long providerId, LocalDate date) {
        return slotRepository.findAvailableByProviderAndDate(providerId, date).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<SlotResponse> getUpcomingAvailableSlots(Long providerId) {
        return slotRepository.findUpcomingAvailableByProvider(providerId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // PDF: getSlotById()
    public SlotResponse getSlotById(Long slotId) {
        return slotRepository.findById(slotId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + slotId));
    }

    // PDF: updateSlot() — provider can edit date/time of an unbooked slot
    public SlotResponse updateSlot(Long slotId, SlotRequest request) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + slotId));
        if (slot.getIsBooked()) throw new RuntimeException("Cannot update a booked slot");
        if (request.getDate() != null) slot.setDate(request.getDate());
        if (request.getStartTime() != null) slot.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) slot.setEndTime(request.getEndTime());
        if (request.getDurationMinutes() != null) slot.setDurationMinutes(request.getDurationMinutes());
        return mapToResponse(slotRepository.save(slot));
    }

    // PDF: bookSlot() — transitions Available → Booked (called by appointment-service)
    public SlotResponse bookSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + slotId));
        if (slot.getIsBooked()) throw new RuntimeException("Slot already booked");
        if (slot.getIsBlocked()) throw new RuntimeException("Slot is blocked");
        slot.setIsBooked(true);
        return mapToResponse(slotRepository.save(slot));
    }

    // PDF: unblockSlot() — releases slot back to Available on cancellation
    public SlotResponse releaseSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + slotId));
        slot.setIsBooked(false);
        return mapToResponse(slotRepository.save(slot));
    }

    // PDF: blockSlot() — provider marks unavailable (leave, personal)
    public SlotResponse blockSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + slotId));
        slot.setIsBlocked(true);
        return mapToResponse(slotRepository.save(slot));
    }

    // PDF: unblockSlot()
    public SlotResponse unblockSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + slotId));
        slot.setIsBlocked(false);
        return mapToResponse(slotRepository.save(slot));
    }

    // PDF: deleteSlot()
    public void deleteSlot(Long slotId) {
        slotRepository.deleteById(slotId);
    }

    private AvailabilitySlot buildSlot(SlotRequest r) {
        return AvailabilitySlot.builder()
                .providerId(r.getProviderId())
                .date(r.getDate())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .durationMinutes(r.getDurationMinutes() != null ? r.getDurationMinutes() : 30)
                .recurrence(r.getRecurrence() != null ? r.getRecurrence() : Recurrence.NONE)
                .build();
    }

    private SlotResponse mapToResponse(AvailabilitySlot s) {
        return SlotResponse.builder()
                .slotId(s.getSlotId()).providerId(s.getProviderId()).date(s.getDate())
                .startTime(s.getStartTime()).endTime(s.getEndTime())
                .durationMinutes(s.getDurationMinutes()).isBooked(s.getIsBooked())
                .isBlocked(s.getIsBlocked()).recurrence(s.getRecurrence())
                .createdAt(s.getCreatedAt()).build();
    }
}
