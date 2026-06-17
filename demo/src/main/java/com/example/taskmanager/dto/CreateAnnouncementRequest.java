package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class CreateAnnouncementRequest {
    private String title;
    private String content;
    private String targetType; // ALL, CLASS, SUBJECT
    private Long classId;
    private Long subjectId;
}
