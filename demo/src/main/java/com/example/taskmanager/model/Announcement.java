package com.example.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String targetType; // ALL, CLASS, SUBJECT

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass; // For CLASS targeting

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject; // For SUBJECT targeting

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
