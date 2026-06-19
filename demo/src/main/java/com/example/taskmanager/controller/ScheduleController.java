package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CreateScheduleRequest;
import com.example.taskmanager.dto.ScheduleDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<ScheduleDto> createSchedule(@RequestBody CreateScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedule(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<ScheduleDto> updateSchedule(
            @PathVariable Long id,
            @RequestBody CreateScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<List<ScheduleDto>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ScheduleDto>> getByClass(@PathVariable Long classId, Principal principal) {
        if (!canViewClassSchedule(classId, principal)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(scheduleService.getScheduleByClass(classId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ScheduleDto>> getByTeacher(@PathVariable Long teacherId, Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.STUDENT
                || (user.getRole() == Role.TEACHER && !user.getId().equals(teacherId))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(scheduleService.getScheduleByTeacher(teacherId));
    }

    @GetMapping("/class/{classId}/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleDto>> getByClassAndDay(
            @PathVariable Long classId,
            @PathVariable String dayOfWeek,
            Principal principal) {
        if (!canViewClassSchedule(classId, principal)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(scheduleService.getScheduleByClassAndDay(classId, dayOfWeek));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    private boolean canViewClassSchedule(Long classId, Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != Role.STUDENT) {
            return true;
        }
        return user.getStudentClass() != null && user.getStudentClass().getId().equals(classId);
    }
}
