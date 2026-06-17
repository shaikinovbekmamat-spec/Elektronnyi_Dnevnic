package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CreateScheduleRequest;
import com.example.taskmanager.dto.ScheduleDto;
import com.example.taskmanager.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleDto> createSchedule(@RequestBody CreateScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedule(request));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ScheduleDto>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(scheduleService.getScheduleByClass(classId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ScheduleDto>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(scheduleService.getScheduleByTeacher(teacherId));
    }

    @GetMapping("/class/{classId}/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleDto>> getByClassAndDay(
            @PathVariable Long classId,
            @PathVariable String dayOfWeek) {
        return ResponseEntity.ok(scheduleService.getScheduleByClassAndDay(classId, dayOfWeek));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}