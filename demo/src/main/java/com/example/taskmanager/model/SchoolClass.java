package com.example.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "classes")
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // например "10А"

    @Column(nullable = false)
    private Integer year; // учебный год

    @ManyToOne
    @JoinColumn(name = "class_teacher_id")
    private User classTeacher; // классный руководитель
}