package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class UploadVideoRequest {
    private String title;
    private Long subjectId;
    private Long classId;
    private Long teacherId;
}
