package com.app.caresync.controller;

import com.app.caresync.model.MedicalRecord;
import com.app.caresync.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records")
public class RecordResource {

    @Autowired
    private RecordService recordService;

    @PostMapping("/create")
    public ResponseEntity<MedicalRecord> create(@RequestBody MedicalRecord record) {
        return ResponseEntity.ok(recordService.createRecord(record));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecord> getByAppointment(@PathVariable Long appointmentId) {
        return recordService.getByAppointment(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(recordService.getByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<MedicalRecord>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(recordService.getByProvider(providerId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MedicalRecord>> getAll() {
        return ResponseEntity.ok(recordService.getAllRecords());
    }
}
