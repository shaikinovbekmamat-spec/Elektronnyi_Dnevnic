package com.example.taskmanager.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SubmitTestRequest {
    private Long studentId;
    private Map<Long, String> answers; // Question ID -> Answer
}
