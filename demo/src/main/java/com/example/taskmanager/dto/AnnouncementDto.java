package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementDto {
    private Long id;
    private String authorName;
    private String title;
    private String content;
    private String targetType;
    private String className;
    private String subjectName;
    private LocalDateTime createdAt;
}
