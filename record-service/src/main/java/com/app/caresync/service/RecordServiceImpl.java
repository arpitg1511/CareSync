package com.app.caresync.service;

import com.app.caresync.model.MedicalRecord;
import com.app.caresync.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordRepository recordRepository;

    @Override
    public MedicalRecord createRecord(MedicalRecord record) {
        return recordRepository.save(record);
    }

    @Override
    public Optional<MedicalRecord> getByAppointment(Long appointmentId) {
        return recordRepository.findByAppointmentId(appointmentId);
    }

    @Override
    public List<MedicalRecord> getByPatient(Long patientId) {
        return recordRepository.findByPatientId(patientId);
    }

    @Override
    public List<MedicalRecord> getByProvider(Long providerId) {
        return recordRepository.findByProviderId(providerId);
    }

    @Override
    public List<MedicalRecord> getAllRecords() {
        return recordRepository.findAll();
    }
}
