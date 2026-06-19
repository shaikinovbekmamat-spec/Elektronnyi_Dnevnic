package com.example.taskmanager.service;

import com.example.taskmanager.dto.TeacherRatingDto;
import java.util.List;

public interface TeacherRatingService {
    List<TeacherRatingDto> getTeacherRatings(Long studentId);
    void rateTeacher(String studentUsername, Long teacherId, Integer rating);
}
