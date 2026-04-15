package com.app.caresync.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "availability_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    @Column(nullable = false)
    private Long providerId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Builder.Default
    private Boolean isBooked = false;

    @Builder.Default
    private Boolean isBlocked = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Recurrence recurrence = Recurrence.NONE;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
