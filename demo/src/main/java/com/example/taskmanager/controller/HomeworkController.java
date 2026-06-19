package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CreateHomeworkRequest;
import com.example.taskmanager.dto.HomeworkDto;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.HomeworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final UserRepository userRepository;

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<HomeworkDto> createHomework(
            @RequestPart("request") CreateHomeworkRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        var currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        request.setTeacherId(currentUser.getId());
        return ResponseEntity.ok(homeworkService.createHomework(request, file));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<HomeworkDto> updateHomework(
            @PathVariable Long id,
            @RequestPart("request") CreateHomeworkRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        var currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        request.setTeacherId(currentUser.getId());
        return ResponseEntity.ok(homeworkService.updateHomework(id, request, file));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<HomeworkDto>> getHomeworkByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(homeworkService.getHomeworkByClass(classId));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<List<HomeworkDto>> getHomeworkByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(homeworkService.getHomeworkByTeacher(teacherId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<Void> deleteHomework(@PathVariable Long id) {
        homeworkService.deleteHomework(id);
        return ResponseEntity.noContent().build();
    }
}
