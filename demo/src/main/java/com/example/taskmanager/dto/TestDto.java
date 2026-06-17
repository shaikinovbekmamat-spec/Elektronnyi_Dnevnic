package com.example.taskmanager.dto;

import lombok.Data;
import java.util.List;

@Data
public class TestDto {
    private Long id;
    private String title;
    private String subjectName;
    private String className;
    private String teacherName;
    private Integer duration;
    private String deadline;
    private List<QuestionDto> questions;
}
