package com.example.taskmanager.repository;

import com.example.taskmanager.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudentId(Long studentId);

    List<Grade> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.student.id = :studentId")
    Double findAverageGradeByStudentId(Long studentId);

    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.student.id = :studentId AND g.subject.id = :subjectId")
    Double findAverageGradeByStudentIdAndSubjectId(Long studentId, Long subjectId);
}