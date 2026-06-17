package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MarkAttendanceRequest {
    private Long studentId;
    private Long scheduleId;
    private LocalDate date;
    private String status;
    private String reason;
}
