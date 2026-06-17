package com.example.taskmanager.service;

import com.example.taskmanager.dto.AttendanceDto;
import com.example.taskmanager.dto.MarkAttendanceRequest;
import java.util.List;

public interface AttendanceService {
    AttendanceDto markAttendance(MarkAttendanceRequest request);
    List<AttendanceDto> getAttendanceByStudent(Long studentId);
    List<AttendanceDto> getAttendanceBySchedule(Long scheduleId, String date);
    Long countAbsences(Long studentId, String type);
}
