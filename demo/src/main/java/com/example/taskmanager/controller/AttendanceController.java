package com.example.taskmanager.controller;

import com.example.taskmanager.dto.AttendanceDto;
import com.example.taskmanager.dto.MarkAttendanceRequest;
import com.example.taskmanager.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<?> markAttendance(@RequestBody MarkAttendanceRequest request) {
        try {
            return ResponseEntity.ok(attendanceService.markAttendance(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudent(studentId));
    }

    @GetMapping("/schedule/{scheduleId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<List<AttendanceDto>> getAttendanceBySchedule(
            @PathVariable Long scheduleId,
            @RequestParam String date) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySchedule(scheduleId, date));
    }

    @GetMapping("/student/{studentId}/stats")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'DIRECTOR')")
    public ResponseEntity<Long> getAbsencesCount(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "ABSENT") String type) {
        return ResponseEntity.ok(attendanceService.countAbsences(studentId, type));
    }
}
