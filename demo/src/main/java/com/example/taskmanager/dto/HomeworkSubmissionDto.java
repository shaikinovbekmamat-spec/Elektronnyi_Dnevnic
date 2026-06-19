package com.example.taskmanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HomeworkSubmissionDto {
    private Long id;
    private Long homeworkId;
    private String homeworkTitle;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private Long subjectId;
    private String subjectName;
    private String className;
    private String comment;
    private String filePath;
    private LocalDateTime submittedAt;
}
