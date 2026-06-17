package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class QuestionDto {
    private Long id;
    private String text;
    private String type;
    private String correctAnswer;
}
