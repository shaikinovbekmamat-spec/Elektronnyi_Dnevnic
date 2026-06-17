package com.example.taskmanager.component;

import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // Create Director
            User director = new User();
            director.setUsername("director");
            director.setPassword(passwordEncoder.encode("password"));
            director.setFullName("Иванов Иван Иванович");
            director.setEmail("director@school.com");
            director.setRole(Role.DIRECTOR);
            userRepository.save(director);

            // Create Teacher
            User teacher = new User();
            teacher.setUsername("teacher");
            teacher.setPassword(passwordEncoder.encode("password"));
            teacher.setFullName("Петрова Анна Сергеевна");
            teacher.setEmail("teacher@school.com");
            teacher.setRole(Role.TEACHER);
            userRepository.save(teacher);

            // Create Student
            User student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("password"));
            student.setFullName("Сидоров Алексей");
            student.setEmail("student@school.com");
            student.setRole(Role.STUDENT);
            userRepository.save(student);

            System.out.println("Demo users created: director, teacher, student (password: password)");
        }
    }
}
