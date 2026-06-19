package com.example.taskmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quarter_grade_access")
public class QuarterGradeAccess {

    @Id
    @Column(nullable = false)
    private Integer quarter;

    @Column(nullable = false)
    private boolean open;
}
