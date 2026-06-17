package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TestResultDto {
    private Long id;
    private Long testId;
    private String testTitle;
    private String studentName;
    private Integer score;
    private Integer grade;
    private LocalDateTime completedAt;
}
