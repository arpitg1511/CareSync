package com.app.caresync.service;

import com.app.caresync.model.MedicalRecord;
import java.util.List;
import java.util.Optional;

public interface RecordService {
    MedicalRecord createRecord(MedicalRecord record);
    Optional<MedicalRecord> getByAppointment(Long appointmentId);
    List<MedicalRecord> getByPatient(Long patientId);
    List<MedicalRecord> getByProvider(Long providerId);
    List<MedicalRecord> getAllRecords();
}
