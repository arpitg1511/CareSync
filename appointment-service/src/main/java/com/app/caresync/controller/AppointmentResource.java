package com.app.caresync.controller;

import com.app.caresync.model.Appointment;
import com.app.caresync.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentResource {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<Appointment> book(@RequestBody Appointment appointment) {
        return ResponseEntity.ok(appointmentService.bookAppointment(appointment));
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<Appointment> getById(@PathVariable Long appointmentId) {
        return appointmentService.getById(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Appointment>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(appointmentService.getByProvider(providerId));
    }

    @GetMapping("/provider/{providerId}/date")
    public ResponseEntity<List<Appointment>> getByProviderAndDate(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getByProviderAndDate(providerId, date));
    }

    @PutMapping("/cancel/{appointmentId}")
    public ResponseEntity<?> cancel(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reschedule/{appointmentId}")
    public ResponseEntity<?> reschedule(@PathVariable Long appointmentId, @RequestParam Long newSlotId) {
        appointmentService.rescheduleAppointment(appointmentId, newSlotId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/complete/{appointmentId}")
    public ResponseEntity<?> complete(@PathVariable Long appointmentId) {
        appointmentService.completeAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/upcoming/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getUpcomingByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getUpcomingByPatient(patientId));
    }

    @GetMapping("/count/provider/{providerId}")
    public ResponseEntity<Long> getCount(@PathVariable Long providerId) {
        return ResponseEntity.ok(appointmentService.getAppointmentCount(providerId));
    }
}
