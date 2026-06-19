package com.example.taskmanager.repository;

import com.example.taskmanager.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findBySchoolClassId(Long classId);
    List<Test> findByTeacherId(Long teacherId);
    List<Test> findBySubjectId(Long subjectId);
    void deleteByTeacherId(Long teacherId);
    void deleteBySchoolClassId(Long classId);
}
