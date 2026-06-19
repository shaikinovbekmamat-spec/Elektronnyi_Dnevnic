package com.example.taskmanager.dto;

import com.example.taskmanager.model.Role;
import lombok.Data;

@Data
public class CreateUserRequest {

    private String username;
    private String password;
    private String fullName;
    private String email;
    private Role role;
    private Long studentClassId;
    private Long teacherSubjectId;
}
