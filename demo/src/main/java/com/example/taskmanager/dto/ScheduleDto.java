package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class ScheduleDto {
    private Long id;
    private String className;
    private String subjectName;
    private String teacherName;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
}