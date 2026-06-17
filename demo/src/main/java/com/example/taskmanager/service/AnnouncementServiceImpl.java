package com.example.taskmanager.service;

import com.example.taskmanager.dto.AnnouncementDto;
import com.example.taskmanager.dto.CreateAnnouncementRequest;
import com.example.taskmanager.model.Announcement;
import com.example.taskmanager.repository.AnnouncementRepository;
import com.example.taskmanager.repository.SchoolClassRepository;
import com.example.taskmanager.repository.SubjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public AnnouncementDto createAnnouncement(CreateAnnouncementRequest request, String authorUsername) {
        var author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new RuntimeException("Автор не найден"));

        Announcement announcement = new Announcement();
        announcement.setAuthor(author);
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setTargetType(request.getTargetType());
        announcement.setCreatedAt(LocalDateTime.now());

        if ("CLASS".equalsIgnoreCase(request.getTargetType()) && request.getClassId() != null) {
            var schoolClass = schoolClassRepository.findById(request.getClassId())
                    .orElseThrow(() -> new RuntimeException("Класс не найден"));
            announcement.setSchoolClass(schoolClass);
        } else if ("SUBJECT".equalsIgnoreCase(request.getTargetType()) && request.getSubjectId() != null) {
            var subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Предмет не найден"));
            announcement.setSubject(subject);
        }

        return toDto(announcementRepository.save(announcement));
    }

    @Override
    public List<AnnouncementDto> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnnouncementDto> getAnnouncementsForClass(Long classId) {
        return announcementRepository.findBySchoolClassId(classId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    private AnnouncementDto toDto(Announcement announcement) {
        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setTargetType(announcement.getTargetType());
        dto.setAuthorName(announcement.getAuthor().getFullName());
        dto.setCreatedAt(announcement.getCreatedAt());
        if (announcement.getSchoolClass() != null) {
            dto.setClassName(announcement.getSchoolClass().getName());
        }
        if (announcement.getSubject() != null) {
            dto.setSubjectName(announcement.getSubject().getName());
        }
        return dto;
    }
}
