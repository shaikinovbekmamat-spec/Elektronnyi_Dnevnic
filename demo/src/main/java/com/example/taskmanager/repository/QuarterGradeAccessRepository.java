package com.example.taskmanager.repository;

import com.example.taskmanager.model.QuarterGradeAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuarterGradeAccessRepository extends JpaRepository<QuarterGradeAccess, Integer> {
}
