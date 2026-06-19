package com.example.taskmanager.service;

import com.example.taskmanager.dto.TeacherRatingDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.TeacherRating;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TeacherRatingRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherRatingServiceImpl implements TeacherRatingService {

    private final TeacherRatingRepository teacherRatingRepository;
    private final UserRepository userRepository;

    @Override
    public List<TeacherRatingDto> getTeacherRatings(Long studentId) {
        return userRepository.findByRole(Role.TEACHER)
                .stream()
                .map(teacher -> {
                    Integer currentRating = studentId == null ? null
                            : teacherRatingRepository.findByStudentIdAndTeacherId(studentId, teacher.getId())
                                    .map(TeacherRating::getRating)
                                    .orElse(null);
                    return new TeacherRatingDto(
                            teacher.getId(),
                            teacher.getFullName(),
                            teacher.getTeacherSubject() != null ? teacher.getTeacherSubject().getName() : null,
                            teacherRatingRepository.findAverageRatingByTeacherId(teacher.getId()),
                            teacherRatingRepository.countByTeacherId(teacher.getId()),
                            currentRating
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void rateTeacher(String studentUsername, Long teacherId, Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be from 1 to 5");
        }

        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can rate teachers");
        }

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("Only teachers can be rated");
        }

        TeacherRating teacherRating = teacherRatingRepository.findByStudentIdAndTeacherId(student.getId(), teacherId)
                .orElseGet(() -> {
                    TeacherRating newRating = new TeacherRating();
                    newRating.setStudent(student);
                    newRating.setTeacher(teacher);
                    return newRating;
                });
        teacherRating.setRating(rating);
        teacherRating.setUpdatedAt(LocalDateTime.now());
        teacherRatingRepository.save(teacherRating);
    }
}
