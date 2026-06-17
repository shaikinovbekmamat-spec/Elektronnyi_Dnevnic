package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GradeDto {
    private Long id;
    private String studentName;
    private String teacherName;
    private String subjectName;
    private Integer value;
    private String comment;
    private LocalDate date;
}