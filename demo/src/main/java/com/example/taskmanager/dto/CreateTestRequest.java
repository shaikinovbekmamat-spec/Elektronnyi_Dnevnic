package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateTestRequest {
    private String title;
    private Long subjectId;
    private Long classId;
    private Long teacherId;
    private Integer duration;
    private LocalDateTime deadline;
    private List<QuestionDto> questions;
}
