package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateScheduleRequest;
import com.example.taskmanager.dto.ScheduleDto;
import java.util.List;

public interface ScheduleService {
    ScheduleDto createSchedule(CreateScheduleRequest request);
    List<ScheduleDto> getScheduleByClass(Long classId);
    List<ScheduleDto> getScheduleByTeacher(Long teacherId);
    List<ScheduleDto> getScheduleByClassAndDay(Long classId, String dayOfWeek);
    void deleteSchedule(Long id);
}
