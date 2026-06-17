package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CreateGradeRequest;
import com.example.taskmanager.dto.GradeDto;
import com.example.taskmanager.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    public ResponseEntity<GradeDto> createGrade(
            @RequestBody CreateGradeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gradeService.createGrade(request, userDetails.getUsername()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeDto>> getGradesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }

    @GetMapping("/average/{studentId}")
    public ResponseEntity<Double> getAverageGrade(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getAverageGrade(studentId));
    }

    @GetMapping("/average/{studentId}/subject/{subjectId}")
    public ResponseEntity<Double> getAverageBySubject(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        return ResponseEntity.ok(gradeService.getAverageGradeBySubject(studentId, subjectId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}