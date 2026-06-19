package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class ScheduleDto {
    private Long id;
    private Long classId;
    private String className;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
}
