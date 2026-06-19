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
@Table(
        name = "teacher_ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "teacher_id"})
)
public class TeacherRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
