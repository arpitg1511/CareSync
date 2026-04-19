package com.app.caresync.dto;

import com.app.caresync.model.Recurrence;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SlotRequest {
    private Long providerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private Recurrence recurrence;
    // For bulk/recurring generation
    private LocalDate endDate;     // used for range
    private String recurrencePattern; // DAILY or WEEKLY
    private Integer recurrenceWeeks;  // Number of weeks to repeat
    private java.util.List<Integer> daysOfWeek; // 0=Sun, 1=Mon, etc.
}
