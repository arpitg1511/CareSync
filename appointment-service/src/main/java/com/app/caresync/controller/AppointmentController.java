package com.app.caresync.controller;

import com.app.caresync.dto.AppointmentRequest;
import com.app.caresync.dto.AppointmentResponse;
import com.app.caresync.model.AppointmentStatus;
import com.app.caresync.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointment Controller", description = "Endpoints for managing medical appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // PDF: POST book
    @PostMapping("/book")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Book a new appointment", description = "Allows a patient to book a slot for a provider")
    public ResponseEntity<AppointmentResponse> book(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.bookAppointment(request));
    }

    // PDF: GET by patient
    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentResponse>> getMyList() {
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    // PDF: GET upcoming by patient
    @GetMapping("/my/upcoming")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentResponse>> getUpcoming() {
        return ResponseEntity.ok(appointmentService.getUpcomingByPatient());
    }

    // PDF: GET by id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    // PDF: GET by provider
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getSchedule(@PathVariable Long providerId) {
        return ResponseEntity.ok(appointmentService.getProviderSchedule(providerId));
    }

    // PDF: GET by provider + date (today)
    @GetMapping("/provider/{providerId}/today")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getTodaySchedule(@PathVariable Long providerId) {
        return ResponseEntity.ok(appointmentService.getProviderTodaySchedule(providerId));
    }

    // PDF: PUT cancel
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    // PDF: PUT reschedule
    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> reschedule(@PathVariable Long id,
            @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, request));
    }

    // PDF: PUT complete
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable Long id,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id, notes != null ? notes : ""));
    }

    // PDF: PUT no-show
    @PutMapping("/{id}/no-show")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> noShow(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.markNoShow(id));
    }

    // PDF: PUT updateStatus
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponse> updateStatus(@PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    // PDF: GET admin all
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getAllForAdmin() {
        return ResponseEntity.ok(appointmentService.getAllForAdmin());
    }

    // PDF: GET count
    @GetMapping("/provider/{providerId}/count")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> getCount(@PathVariable Long providerId) {
        return ResponseEntity.ok(appointmentService.getAppointmentStats(providerId));
    }
}
