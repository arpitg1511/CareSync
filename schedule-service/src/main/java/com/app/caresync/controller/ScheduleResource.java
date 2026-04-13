package com.app.caresync.controller;

import com.app.caresync.model.AvailabilitySlot;
import com.app.caresync.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/slots")
public class ScheduleResource {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/add")
    public ResponseEntity<AvailabilitySlot> addSlot(@RequestBody AvailabilitySlot slot) {
        return ResponseEntity.ok(scheduleService.addSlot(slot));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AvailabilitySlot>> addBulkSlots(@RequestBody List<AvailabilitySlot> slots) {
        return ResponseEntity.ok(scheduleService.addBulkSlots(slots));
    }

    @PostMapping("/generateRecurring")
    public ResponseEntity<List<AvailabilitySlot>> generateRecurring(
            @RequestParam Long providerId,
            @RequestParam String pattern,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(scheduleService.generateRecurringSlots(providerId, pattern, startDate, endDate));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<AvailabilitySlot>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(scheduleService.getSlotsByProvider(providerId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<AvailabilitySlot>> getAvailable(
            @RequestParam Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getAvailableSlots(providerId, date));
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<AvailabilitySlot> getById(@PathVariable Long slotId) {
        return scheduleService.getSlotById(slotId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/block/{slotId}")
    public ResponseEntity<?> blockSlot(@PathVariable Long slotId) {
        scheduleService.blockSlot(slotId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/unblock/{slotId}")
    public ResponseEntity<?> unblockSlot(@PathVariable Long slotId) {
        scheduleService.unblockSlot(slotId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<?> deleteSlot(@PathVariable Long slotId) {
        scheduleService.deleteSlot(slotId);
        return ResponseEntity.ok().build();
    }
}
