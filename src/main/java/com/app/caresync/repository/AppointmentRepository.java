package com.app.caresync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.app.caresync.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>{
	
	List<Appointment> findByPatientId(Long patientId);
	List<Appointment> findByProviderId(Long providerId);
	
}
