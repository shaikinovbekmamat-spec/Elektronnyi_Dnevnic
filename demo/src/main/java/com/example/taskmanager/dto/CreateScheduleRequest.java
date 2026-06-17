package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class CreateScheduleRequest {
    private Long classId;
    private Long subjectId;
    private Long teacherId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
}