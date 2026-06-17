package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CreateTestRequest;
import com.example.taskmanager.dto.SubmitTestRequest;
import com.example.taskmanager.dto.TestDto;
import com.example.taskmanager.dto.TestResultDto;
import com.example.taskmanager.service.TestingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestingService testingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<TestDto> createTest(@RequestBody CreateTestRequest request) {
        return ResponseEntity.ok(testingService.createTest(request));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<TestDto>> getTestsByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(testingService.getTestsByClass(classId));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<TestResultDto> submitTest(
            @PathVariable Long id,
            @RequestBody SubmitTestRequest request) {
        return ResponseEntity.ok(testingService.submitTest(id, request));
    }

    @GetMapping("/results/student/{studentId}")
    public ResponseEntity<List<TestResultDto>> getResultsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(testingService.getResultsByStudent(studentId));
    }
}
