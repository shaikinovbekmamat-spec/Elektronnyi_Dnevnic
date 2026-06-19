package com.example.taskmanager.controller;

import com.example.taskmanager.dto.AnnouncementDto;
import com.example.taskmanager.dto.CreateAnnouncementRequest;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<AnnouncementDto> createAnnouncement(
            @RequestBody CreateAnnouncementRequest request,
            Principal principal) {
        return ResponseEntity.ok(announcementService.createAnnouncement(request, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementDto>> getAllAnnouncements(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (user.getRole() == Role.STUDENT) {
            if (user.getStudentClass() == null) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(announcementService.getVisibleAnnouncementsForStudent(
                    user.getStudentClass().getId(), null));
        }
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<AnnouncementDto>> getAnnouncementsByClass(@PathVariable Long classId, Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (user.getRole() == Role.STUDENT
                && (user.getStudentClass() == null || !user.getStudentClass().getId().equals(classId))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(announcementService.getAnnouncementsForClass(classId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}
