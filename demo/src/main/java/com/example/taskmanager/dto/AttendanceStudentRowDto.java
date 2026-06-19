package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class AttendanceStudentRowDto {
    private Long attendanceId;
    private Long studentId;
    private String studentName;
    private String status;
    private String reason;
    private boolean editableToday;
}
