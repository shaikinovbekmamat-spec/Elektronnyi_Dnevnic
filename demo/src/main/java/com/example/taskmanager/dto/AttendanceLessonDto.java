package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalTime;
import java.util.List;

@Data
public class AttendanceLessonDto {
    private Long scheduleId;
    private Long classId;
    private String className;
    private String subjectName;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private List<AttendanceStudentRowDto> rows;
}
