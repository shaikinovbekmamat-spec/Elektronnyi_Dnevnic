package com.example.taskmanager.repository;

import com.example.taskmanager.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByStudentId(Long studentId);
    List<TestResult> findByTestId(Long testId);
    List<TestResult> findByTestIdAndStudentId(Long testId, Long studentId);
    void deleteByStudentId(Long studentId);
    void deleteByTestTeacherId(Long teacherId);
    void deleteByTestSchoolClassId(Long classId);
}
