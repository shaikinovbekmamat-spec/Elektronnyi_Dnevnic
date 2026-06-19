package com.example.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRatingDto {

    private Long teacherId;
    private String teacherName;
    private String subjectName;
    private Double averageRating;
    private long ratingCount;
    private Integer currentStudentRating;
}
