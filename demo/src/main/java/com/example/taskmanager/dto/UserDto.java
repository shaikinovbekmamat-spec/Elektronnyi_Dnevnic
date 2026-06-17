package com.example.taskmanager.dto;

import com.example.taskmanager.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    // пароль НЕ включаем намеренно!
}