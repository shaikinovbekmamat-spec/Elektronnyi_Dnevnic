package com.example.taskmanager.repository;

import com.example.taskmanager.model.VideoLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoLessonRepository extends JpaRepository<VideoLesson, Long> {
    List<VideoLesson> findBySchoolClassId(Long classId);
    List<VideoLesson> findBySubjectId(Long subjectId);
    List<VideoLesson> findByTeacherId(Long teacherId);
    void deleteByTeacherId(Long teacherId);
    void deleteBySchoolClassId(Long classId);
}
