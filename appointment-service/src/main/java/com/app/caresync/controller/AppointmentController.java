package com.app.caresync.controller;

import com.app.caresync.dto.AppointmentRequest;
import com.app.caresync.dto.AppointmentResponse;
import com.app.caresync.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // 📅 Book Appointment
    @PostMapping("/book")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> book(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.bookAppointment(request));
    }

    // 👤 Get Patient Appointments
    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentResponse>> getMyList() {
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    // 👨‍⚕️ Get Provider Schedule
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getSchedule(@PathVariable Long providerId) {
        return ResponseEntity.ok(appointmentService.getProviderSchedule(providerId));
    }

    // 🚫 Cancel Appointment
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    // ✅ Complete Appointment
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable Long id, @RequestParam String notes) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id, notes));
    }
}
