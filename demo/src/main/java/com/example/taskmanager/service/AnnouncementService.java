package com.example.taskmanager.service;

import com.example.taskmanager.dto.AnnouncementDto;
import com.example.taskmanager.dto.CreateAnnouncementRequest;
import java.util.List;

public interface AnnouncementService {
    AnnouncementDto createAnnouncement(CreateAnnouncementRequest request, String authorUsername);
    List<AnnouncementDto> getAllAnnouncements();
    List<AnnouncementDto> getVisibleAnnouncementsForStudent(Long classId, Long subjectId);
    List<AnnouncementDto> getAnnouncementsForClass(Long classId);
    void deleteAnnouncement(Long id);
}
