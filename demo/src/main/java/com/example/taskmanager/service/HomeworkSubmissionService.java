package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateHomeworkSubmissionRequest;
import com.example.taskmanager.dto.HomeworkSubmissionDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface HomeworkSubmissionService {
    HomeworkSubmissionDto submitHomework(CreateHomeworkSubmissionRequest request, MultipartFile file, String studentUsername) throws IOException;
    List<HomeworkSubmissionDto> getSubmissionsByStudent(Long studentId);
    List<HomeworkSubmissionDto> getSubmissionsByTeacher(Long teacherId);
    List<HomeworkSubmissionDto> getAllSubmissions();
}
