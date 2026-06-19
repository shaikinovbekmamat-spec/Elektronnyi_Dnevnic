package com.example.taskmanager.repository;

import com.example.taskmanager.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTargetType(String targetType);
    List<Announcement> findBySchoolClassId(Long classId);
    List<Announcement> findBySubjectId(Long subjectId);

    @Query("""
            SELECT a FROM Announcement a
            WHERE UPPER(a.targetType) IN ('ALL', 'GENERAL')
               OR (UPPER(a.targetType) = 'CLASS' AND a.schoolClass.id = :classId)
               OR (:subjectId IS NOT NULL AND UPPER(a.targetType) = 'SUBJECT' AND a.subject.id = :subjectId)
            ORDER BY a.createdAt DESC
            """)
    List<Announcement> findVisibleForStudent(Long classId, Long subjectId);

    void deleteByAuthorId(Long authorId);
    void deleteBySchoolClassId(Long classId);
}
