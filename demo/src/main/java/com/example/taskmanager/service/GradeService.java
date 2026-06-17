package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateGradeRequest;
import com.example.taskmanager.dto.GradeDto;
import java.util.List;

public interface GradeService {
    GradeDto createGrade(CreateGradeRequest request, String teacherUsername);
    List<GradeDto> getGradesByStudent(Long studentId);
    Double getAverageGrade(Long studentId);
    Double getAverageGradeBySubject(Long studentId, Long subjectId);
    void deleteGrade(Long id);
}