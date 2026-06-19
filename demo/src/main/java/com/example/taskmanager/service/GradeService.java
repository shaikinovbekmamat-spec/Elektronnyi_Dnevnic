package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateGradeRequest;
import com.example.taskmanager.dto.GradeDto;
import java.util.List;

public interface GradeService {
    GradeDto createGrade(CreateGradeRequest request, String teacherUsername);
    GradeDto updateGrade(Long id, CreateGradeRequest request, String teacherUsername);
    List<GradeDto> getGradesByStudent(Long studentId);
    List<GradeDto> getGradesByClass(Long classId);
    List<GradeDto> getGradesByClassAndSubject(Long classId, Long subjectId);
    List<GradeDto> getGradesByClassSubjectAndQuarter(Long classId, Long subjectId, Integer quarter);
    Double getAverageGrade(Long studentId);
    Double getAverageGradeBySubject(Long studentId, Long subjectId);
    boolean isQuarterOpen(Integer quarter);
    void setQuarterAccess(Integer quarter, boolean open);
    void deleteGrade(Long id);
}
