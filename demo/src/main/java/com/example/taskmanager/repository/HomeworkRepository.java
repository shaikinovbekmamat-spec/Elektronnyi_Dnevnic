package com.example.taskmanager.repository;

import com.example.taskmanager.model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findBySchoolClassId(Long classId);
    List<Homework> findByTeacherId(Long teacherId);
    List<Homework> findBySubjectId(Long subjectId);
}
