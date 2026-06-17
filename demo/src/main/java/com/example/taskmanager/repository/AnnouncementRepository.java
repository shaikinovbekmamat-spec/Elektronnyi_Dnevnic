package com.example.taskmanager.repository;

import com.example.taskmanager.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTargetType(String targetType);
    List<Announcement> findBySchoolClassId(Long classId);
    List<Announcement> findBySubjectId(Long subjectId);
}
