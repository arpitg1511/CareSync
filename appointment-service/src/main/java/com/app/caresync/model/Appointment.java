package com.app.caresync.model;

import java.time.LocalDateTime;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Appointment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long appointmentId;
	
	@Column(nullable = false, unique = true)
	private Long patientId;
	
	@Column(nullable = false, unique = true)
	private Long providerId;
	
	@Column(nullable = false)
	private LocalDateTime appointmentDateTime;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AppointmentStatus status;
	private String reason;
	private String doctorNotes;
}
