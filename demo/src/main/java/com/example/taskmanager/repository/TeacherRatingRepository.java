package com.example.taskmanager.repository;

import com.example.taskmanager.model.TeacherRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TeacherRatingRepository extends JpaRepository<TeacherRating, Long> {

    Optional<TeacherRating> findByStudentIdAndTeacherId(Long studentId, Long teacherId);

    @Query("SELECT AVG(r.rating) FROM TeacherRating r WHERE r.teacher.id = :teacherId")
    Double findAverageRatingByTeacherId(Long teacherId);

    long countByTeacherId(Long teacherId);

    void deleteByStudentIdOrTeacherId(Long studentId, Long teacherId);
}
