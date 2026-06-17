package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateHomeworkRequest {
    private Long subjectId;
    private Long classId;
    private Long teacherId;
    private String title;
    private String description;
    private LocalDate dueDate;
}
