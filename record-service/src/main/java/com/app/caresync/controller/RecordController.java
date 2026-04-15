package com.app.caresync.controller;

import com.app.caresync.dto.RecordRequest;
import com.app.caresync.dto.RecordResponse;
import com.app.caresync.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    @Autowired
    private RecordService recordService;

    // 📝 Create medical record (doctor after completing appointment)
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<RecordResponse> create(@RequestBody RecordRequest request) {
        return ResponseEntity.ok(recordService.createRecord(request));
    }

    // 📋 Get record by appointment
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<RecordResponse> getByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(recordService.getRecordByAppointment(appointmentId));
    }

    // 👤 Patient's own records
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<RecordResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(recordService.getRecordsByPatient(patientId));
    }

    // 👨‍⚕️ Provider's created records
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<RecordResponse>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(recordService.getRecordsByProvider(providerId));
    }

    // 🔍 Get record by ID
    @GetMapping("/{recordId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<RecordResponse> getById(@PathVariable Long recordId) {
        return ResponseEntity.ok(recordService.getRecordById(recordId));
    }

    // ✏️ Update record (within allowed edit window)
    @PutMapping("/{recordId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<RecordResponse> update(@PathVariable Long recordId, @RequestBody RecordRequest request) {
        return ResponseEntity.ok(recordService.updateRecord(recordId, request));
    }

    // 👑 Admin: view all records (audit compliance)
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RecordResponse>> getAll() {
        return ResponseEntity.ok(recordService.getAllRecords());
    }

    // 🗑️ Delete record
    @DeleteMapping("/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long recordId) {
        recordService.deleteRecord(recordId);
        return ResponseEntity.noContent().build();
    }
    // PDF: GET followUp records (today's follow-ups)
    @GetMapping("/follow-ups/today")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<RecordResponse>> getFollowUps() {
        return ResponseEntity.ok(recordService.getFollowUpRecords());
    }

    // PDF: GET record count for a patient
    @GetMapping("/patient/{patientId}/count")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<java.util.Map<String, Long>> getCount(@PathVariable Long patientId) {
        return ResponseEntity.ok(java.util.Map.of("count", recordService.getRecordCount(patientId)));
    }

    // PDF: PUT attachDocument
    @PutMapping("/{recordId}/attach")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<RecordResponse> attachDocument(@PathVariable Long recordId,
            @RequestParam String attachmentUrl) {
        return ResponseEntity.ok(recordService.attachDocument(recordId, attachmentUrl));
    }


}
