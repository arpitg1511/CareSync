package com.app.caresync.service;

import com.app.caresync.dto.SlotRequest;
import com.app.caresync.dto.SlotResponse;
import com.app.caresync.exception.SlotNotFoundException;
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
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private SlotRepository slotRepository;

    @Override
    public SlotResponse addSlot(SlotRequest request) {
        AvailabilitySlot slot = buildSlot(request);
        return mapToResponse(slotRepository.save(slot));
    }

    @Override
    @jakarta.transaction.Transactional
    public List<SlotResponse> addBulkSlots(List<SlotRequest> requests) {
        if (requests.isEmpty()) return new java.util.ArrayList<>();
        
        Long providerId = requests.get(0).getProviderId();
        LocalDate date = requests.get(0).getDate();
        
        // Atomic wipe-and-re-sync: Delete existing slots for this provider and date exactly before generating new ones.
        slotRepository.deleteByProviderIdAndDate(providerId, date);

        List<AvailabilitySlot> slots = requests.stream()
                .map(r -> {
                    if (r.getDurationMinutes() != null && r.getDurationMinutes() > 120) {
                        throw new RuntimeException("Validation Error: Shift duration cannot exceed 120 minutes (2 hours).");
                    }
                    if (r.getStartTime() != null && r.getEndTime() != null && r.getStartTime().isAfter(r.getEndTime())) {
                        throw new RuntimeException("Validation Error: Start time cannot be after end time.");
                    }
                    return buildSlot(r);
                })
                .collect(Collectors.toList());
                
        return slotRepository.saveAll(slots).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public List<SlotResponse> generateRecurringSlots(SlotRequest request) {
        List<AvailabilitySlot> slots = new ArrayList<>();
        LocalDate start = LocalDate.now();
        int weeks = request.getRecurrenceWeeks() != null ? request.getRecurrenceWeeks() : 4;
        List<Integer> targetDays = request.getDaysOfWeek(); // 0=Sun, 1=Mon... 6=Sat

        if (targetDays == null || targetDays.isEmpty()) {
            targetDays = List.of(start.getDayOfWeek().getValue() % 7);
        }

        LocalDate end = start.plusWeeks(weeks);
        LocalDate current = start;

        while (current.isBefore(end)) {
            // Check if current day is one of the target days
            // java.time.DayOfWeek: 1 (Mon) to 7 (Sun)
            // our index: 0=Sun, 1=Mon, ..., 6=Sat
            int currentDayIdx = current.getDayOfWeek().getValue() % 7;
            
            if (targetDays.contains(currentDayIdx)) {
                // Atomic wipe for this day
                slotRepository.deleteByProviderIdAndDate(request.getProviderId(), current);

                // Generate shards
                java.time.LocalTime currentT = request.getStartTime();
                java.time.LocalTime endT = request.getEndTime();
                int interval = request.getDurationMinutes() != null ? request.getDurationMinutes() : 30;

                while (currentT.isBefore(endT)) {
                    java.time.LocalTime nextT = currentT.plusMinutes(interval);
                    if (nextT.isAfter(endT)) break;

                    slots.add(AvailabilitySlot.builder()
                            .providerId(request.getProviderId())
                            .date(current)
                            .startTime(currentT)
                            .endTime(nextT)
                            .durationMinutes(interval)
                            .isBooked(false)
                            .isBlocked(false)
                            .recurrence(Recurrence.WEEKLY)
                            .build());
                    currentT = nextT;
                }
            }
            current = current.plusDays(1);
        }

        return slotRepository.saveAll(slots).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<SlotResponse> getSlotsByProvider(Long providerId) {
        return slotRepository.findByProviderId(providerId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<SlotResponse> getAvailableSlotsByProviderAndDate(Long providerId, LocalDate date) {
        return slotRepository.findAvailableByProviderAndDate(providerId, date).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<SlotResponse> getUpcomingAvailableSlots(Long providerId) {
        return slotRepository.findUpcomingAvailableByProvider(providerId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public SlotResponse getSlotById(Long slotId) {
        return slotRepository.findById(slotId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found: " + slotId));
    }

    @Override
    public SlotResponse updateSlot(Long slotId, SlotRequest request) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found: " + slotId));
        if (Boolean.TRUE.equals(slot.getIsBooked())) throw new RuntimeException("Cannot update a booked slot");
        if (request.getDate() != null) slot.setDate(request.getDate());
        if (request.getStartTime() != null) slot.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) slot.setEndTime(request.getEndTime());
        if (request.getDurationMinutes() != null) slot.setDurationMinutes(request.getDurationMinutes());
        return mapToResponse(slotRepository.save(slot));
    }

    @Override
    public SlotResponse bookSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found: " + slotId));
        if (Boolean.TRUE.equals(slot.getIsBooked())) throw new RuntimeException("Slot already booked");
        if (Boolean.TRUE.equals(slot.getIsBlocked())) throw new RuntimeException("Slot is blocked");
        slot.setIsBooked(true);
        return mapToResponse(slotRepository.save(slot));
    }

    @Override
    public SlotResponse releaseSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found: " + slotId));
        slot.setIsBooked(false);
        return mapToResponse(slotRepository.save(slot));
    }

    @Override
    public SlotResponse blockSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found: " + slotId));
        slot.setIsBlocked(true);
        return mapToResponse(slotRepository.save(slot));
    }

    @Override
    public SlotResponse unblockSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found: " + slotId));
        slot.setIsBlocked(false);
        return mapToResponse(slotRepository.save(slot));
    }

    @Override
    @jakarta.transaction.Transactional
    public void deleteSlot(Long slotId) {
        if (!slotRepository.existsById(slotId)) {
            throw new SlotNotFoundException("Slot not found: " + slotId);
        }
        slotRepository.deleteById(slotId);
    }

    @Override
    @jakarta.transaction.Transactional
    public void deleteSlotsByProviderAndDate(Long providerId, java.time.LocalDate date) {
        slotRepository.deleteByProviderIdAndDate(providerId, date);
    }

    private AvailabilitySlot buildSlot(SlotRequest r) {
        return AvailabilitySlot.builder()
                .providerId(r.getProviderId())
                .date(r.getDate())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .durationMinutes(r.getDurationMinutes() != null ? r.getDurationMinutes() : 30)
                .recurrence(r.getRecurrence() != null ? r.getRecurrence() : Recurrence.NONE)
                .isBooked(false)
                .isBlocked(false)
                .build();
    }

    private SlotResponse mapToResponse(AvailabilitySlot s) {
        if (s == null) return null;
        return SlotResponse.builder()
                .slotId(s.getSlotId()).providerId(s.getProviderId()).date(s.getDate())
                .startTime(s.getStartTime()).endTime(s.getEndTime())
                .durationMinutes(s.getDurationMinutes()).isBooked(s.getIsBooked())
                .isBlocked(s.getIsBlocked()).recurrence(s.getRecurrence())
                .createdAt(s.getCreatedAt()).build();
    }
}
