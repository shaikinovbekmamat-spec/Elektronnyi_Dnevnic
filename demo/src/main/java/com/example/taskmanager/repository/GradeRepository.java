package com.example.taskmanager.repository;

import com.example.taskmanager.model.Grade;
import com.example.taskmanager.model.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudentId(Long studentId);

    List<Grade> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    List<Grade> findByStudentStudentClassId(Long classId);

    List<Grade> findByStudentStudentClassIdAndSubjectId(Long classId, Long subjectId);

    List<Grade> findByStudentStudentClassIdAndSubjectIdAndQuarter(Long classId, Long subjectId, Integer quarter);

    List<Grade> findByStudentIdAndSubjectIdAndQuarterAndGradeType(Long studentId, Long subjectId, Integer quarter, GradeType gradeType);

    java.util.Optional<Grade> findFirstByStudentIdAndSubjectIdAndQuarterAndGradeType(Long studentId, Long subjectId, Integer quarter, GradeType gradeType);

    void deleteByStudentIdOrTeacherId(Long studentId, Long teacherId);

    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.student.id = :studentId")
    Double findAverageGradeByStudentId(Long studentId);

    @Query("SELECT AVG(g.value) FROM Grade g")
    Double findSchoolAverageGrade();

    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.student.studentClass.id = :classId")
    Double findAverageGradeByClassId(Long classId);

    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.student.id = :studentId AND g.subject.id = :subjectId")
    Double findAverageGradeByStudentIdAndSubjectId(Long studentId, Long subjectId);

    @Query("""
            SELECT AVG(g.value) FROM Grade g
            WHERE g.student.id = :studentId
              AND g.subject.id = :subjectId
              AND g.quarter = :quarter
              AND (g.gradeType = com.example.taskmanager.model.GradeType.REGULAR OR g.gradeType IS NULL)
            """)
    Double findRegularAverageByStudentSubjectAndQuarter(Long studentId, Long subjectId, Integer quarter);
}
