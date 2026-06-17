package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AttendanceDto {
    private Long id;
    private String studentName;
    private String subjectName;
    private String startTime;
    private LocalDate date;
    private String status;
    private String reason;
}
