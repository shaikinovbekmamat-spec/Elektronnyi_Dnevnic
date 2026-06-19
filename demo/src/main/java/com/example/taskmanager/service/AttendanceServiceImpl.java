package com.example.taskmanager.service;

import com.example.taskmanager.dto.AttendanceDto;
import com.example.taskmanager.dto.MarkAttendanceRequest;
import com.example.taskmanager.model.Attendance;
import com.example.taskmanager.repository.AttendanceRepository;
import com.example.taskmanager.repository.ScheduleRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public AttendanceDto markAttendance(MarkAttendanceRequest request) {
        if (request.getDate() == null || !LocalDate.now().equals(request.getDate())) {
            throw new RuntimeException("Attendance can be changed only on the current date");
        }
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new RuntimeException("Attendance status is required");
        }
        var student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Ученик не найден"));
        var schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Урок не найден"));

        Attendance attendance = attendanceRepository
                .findFirstByStudentIdAndScheduleIdAndDate(student.getId(), schedule.getId(), request.getDate())
                .orElseGet(Attendance::new);
        attendance.setStudent(student);
        attendance.setSchedule(schedule);
        attendance.setDate(request.getDate());
        attendance.setStatus(request.getStatus());
        attendance.setReason(request.getReason());

        return toDto(attendanceRepository.save(attendance));
    }

    @Override
    public List<AttendanceDto> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDto> getAttendanceBySchedule(Long scheduleId, String date) {
        return attendanceRepository.findByScheduleIdAndDate(scheduleId, LocalDate.parse(date)).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long countAbsences(Long studentId, String type) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .filter(a -> a.getStatus().equalsIgnoreCase(type))
                .count();
    }

    private AttendanceDto toDto(Attendance attendance) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(attendance.getId());
        dto.setStudentName(attendance.getStudent().getFullName());
        dto.setSubjectName(attendance.getSchedule().getSubject().getName());
        dto.setStartTime(attendance.getSchedule().getStartTime().toString());
        dto.setDate(attendance.getDate());
        dto.setStatus(attendance.getStatus());
        dto.setReason(attendance.getReason());
        return dto;
    }
}
