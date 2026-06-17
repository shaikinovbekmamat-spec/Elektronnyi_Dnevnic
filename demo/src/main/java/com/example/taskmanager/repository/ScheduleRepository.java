package com.example.taskmanager.repository;

import com.example.taskmanager.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findBySchoolClassId(Long classId);
    List<Schedule> findByTeacherId(Long teacherId);
    List<Schedule> findBySchoolClassIdAndDayOfWeek(Long classId, String dayOfWeek);
}