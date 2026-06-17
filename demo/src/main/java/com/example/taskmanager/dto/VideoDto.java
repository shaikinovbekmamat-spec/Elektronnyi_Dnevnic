package com.example.taskmanager.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VideoDto {
    private Long id;
    private String title;
    private String subjectName;
    private String className;
    private String teacherName;
    private String filePath;
    private LocalDateTime uploadedAt;
}
