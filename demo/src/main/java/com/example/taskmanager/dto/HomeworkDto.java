package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HomeworkDto {
    private Long id;
    private String subjectName;
    private String className;
    private String teacherName;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String filePath;
}
