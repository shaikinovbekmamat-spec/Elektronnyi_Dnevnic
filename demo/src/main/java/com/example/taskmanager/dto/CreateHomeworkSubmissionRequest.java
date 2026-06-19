package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class CreateHomeworkSubmissionRequest {
    private Long homeworkId;
    private Long teacherId;
    private Long subjectId;
    private String comment;
}
