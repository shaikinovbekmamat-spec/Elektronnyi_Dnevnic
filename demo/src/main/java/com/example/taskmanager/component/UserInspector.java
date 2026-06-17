package com.example.taskmanager.component;

import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInspector implements CommandLineRunner {
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        System.out.println("=== USER INSPECTION ===");
        userRepository.findAll().forEach(u -> {
            System.out.println("User: " + u.getUsername() + " | Role: " + u.getRole() + " | ID: " + u.getId());
        });
        System.out.println("=======================");
    }
}
