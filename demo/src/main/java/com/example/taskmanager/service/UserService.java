package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateUserRequest;
import com.example.taskmanager.dto.UserDto;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> getAllUsers();
    Optional<UserDto> getUserById(Long id);
    UserDto createUser(CreateUserRequest request);
    void deleteUser(Long id);
}