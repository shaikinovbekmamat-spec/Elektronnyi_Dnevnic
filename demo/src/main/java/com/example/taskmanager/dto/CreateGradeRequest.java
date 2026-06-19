package com.example.taskmanager.dto;

import com.example.taskmanager.model.GradeType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateGradeRequest {
    private Long studentId;
    private Long subjectId;
    private Integer value;
    private String comment;
    private LocalDate date;
    private Integer quarter;
    private GradeType gradeType;
}
