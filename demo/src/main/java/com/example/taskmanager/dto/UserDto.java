package com.example.taskmanager.dto;

import com.example.taskmanager.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private Long studentClassId;
    private String studentClassName;
    private Long teacherSubjectId;
    private String teacherSubjectName;
}
