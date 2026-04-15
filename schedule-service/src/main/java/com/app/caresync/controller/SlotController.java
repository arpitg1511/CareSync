package com.app.caresync.controller;

import com.app.caresync.dto.SlotRequest;
import com.app.caresync.dto.SlotResponse;
import com.app.caresync.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    @Autowired
    private ScheduleService scheduleService;

    // PDF: POST add single slot
    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<SlotResponse> addSlot(@RequestBody SlotRequest request) {
        return ResponseEntity.ok(scheduleService.addSlot(request));
    }

    // PDF: POST addBulkSlots
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<SlotResponse>> addBulk(@RequestBody List<SlotRequest> requests) {
        return ResponseEntity.ok(scheduleService.addBulkSlots(requests));
    }

    // PDF: POST generateRecurring
    @PostMapping("/recurring")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<SlotResponse>> generateRecurring(@RequestBody SlotRequest request) {
        return ResponseEntity.ok(scheduleService.generateRecurringSlots(request));
    }

    // PDF: GET by provider
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'PATIENT')")
    public ResponseEntity<List<SlotResponse>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(scheduleService.getSlotsByProvider(providerId));
    }

    // PDF: GET available slots by provider + date (guest/patient browsing)
    @GetMapping("/available")
    public ResponseEntity<List<SlotResponse>> getAvailable(
            @RequestParam Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getAvailableSlotsByProviderAndDate(providerId, date));
    }

    // Upcoming available slots
    @GetMapping("/upcoming/{providerId}")
    public ResponseEntity<List<SlotResponse>> getUpcoming(@PathVariable Long providerId) {
        return ResponseEntity.ok(scheduleService.getUpcomingAvailableSlots(providerId));
    }

    // PDF: GET by id
    @GetMapping("/{slotId}")
    public ResponseEntity<SlotResponse> getById(@PathVariable Long slotId) {
        return ResponseEntity.ok(scheduleService.getSlotById(slotId));
    }

    // PDF: PUT updateSlot
    @PutMapping("/{slotId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<SlotResponse> update(@PathVariable Long slotId, @RequestBody SlotRequest request) {
        return ResponseEntity.ok(scheduleService.updateSlot(slotId, request));
    }

    // PDF: PUT blockSlot
    @PutMapping("/{slotId}/block")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<SlotResponse> block(@PathVariable Long slotId) {
        return ResponseEntity.ok(scheduleService.blockSlot(slotId));
    }

    // PDF: PUT unblockSlot
    @PutMapping("/{slotId}/unblock")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<SlotResponse> unblock(@PathVariable Long slotId) {
        return ResponseEntity.ok(scheduleService.unblockSlot(slotId));
    }

    // Internal: book slot (called by appointment-service)
    @PutMapping("/internal/{slotId}/book")
    public ResponseEntity<SlotResponse> bookSlot(@PathVariable Long slotId) {
        return ResponseEntity.ok(scheduleService.bookSlot(slotId));
    }

    // Internal: release slot on cancellation
    @PutMapping("/internal/{slotId}/release")
    public ResponseEntity<SlotResponse> releaseSlot(@PathVariable Long slotId) {
        return ResponseEntity.ok(scheduleService.releaseSlot(slotId));
    }

    // PDF: DELETE
    @DeleteMapping("/{slotId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long slotId) {
        scheduleService.deleteSlot(slotId);
        return ResponseEntity.noContent().build();
    }
}
