package com.example.taskmanager.repository;

import com.example.taskmanager.model.HomeworkSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkSubmissionRepository extends JpaRepository<HomeworkSubmission, Long> {
    List<HomeworkSubmission> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
    List<HomeworkSubmission> findByTeacherIdOrderBySubmittedAtDesc(Long teacherId);
    List<HomeworkSubmission> findByHomeworkIdOrderBySubmittedAtDesc(Long homeworkId);
    List<HomeworkSubmission> findByStudentIdOrTeacherId(Long studentId, Long teacherId);
    List<HomeworkSubmission> findByHomeworkSchoolClassId(Long classId);
}
