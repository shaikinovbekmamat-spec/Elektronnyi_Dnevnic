package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateHomeworkRequest;
import com.example.taskmanager.dto.HomeworkDto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface HomeworkService {
    HomeworkDto createHomework(CreateHomeworkRequest request, MultipartFile file) throws IOException;
    List<HomeworkDto> getHomeworkByClass(Long classId);
    List<HomeworkDto> getHomeworkByTeacher(Long teacherId);
    void deleteHomework(Long id);
}
