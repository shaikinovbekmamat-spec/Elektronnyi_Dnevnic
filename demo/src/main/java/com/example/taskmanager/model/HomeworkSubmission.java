package com.example.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "homework_submissions")
public class HomeworkSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "homework_id", nullable = false)
    private Homework homework;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime submittedAt;
}
