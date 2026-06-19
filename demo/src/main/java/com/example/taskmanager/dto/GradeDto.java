package com.example.taskmanager.dto;

import com.example.taskmanager.model.GradeType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class GradeDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private Long subjectId;
    private String subjectName;
    private Integer value;
    private String comment;
    private LocalDate date;
    private Integer quarter;
    private GradeType gradeType;
    private boolean editableToday;
}
