package com.example.taskmanager.repository;

import com.example.taskmanager.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByScheduleIdAndDate(Long scheduleId, LocalDate date);
    Optional<Attendance> findFirstByStudentIdAndScheduleIdAndDate(Long studentId, Long scheduleId, LocalDate date);
    List<Attendance> findByStudentIdAndDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);
    void deleteByStudentId(Long studentId);
    void deleteByScheduleTeacherId(Long teacherId);
    void deleteByScheduleSchoolClassId(Long classId);
}
