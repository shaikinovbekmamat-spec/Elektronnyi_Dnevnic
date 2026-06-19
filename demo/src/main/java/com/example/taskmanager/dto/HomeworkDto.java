package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HomeworkDto {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private Long teacherId;
    private String teacherName;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String filePath;
}
