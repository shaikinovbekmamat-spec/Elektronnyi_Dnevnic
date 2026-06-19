package com.example.taskmanager.component;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.SchoolClass;
import com.example.taskmanager.model.Subject;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.SchoolClassRepository;
import com.example.taskmanager.repository.SubjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        boolean emptyDatabase = userRepository.count() == 0;

        ensureDemoUser("director", "director@school.com", "Director", Role.DIRECTOR);
        ensureSchoolClasses();
        ensureSubjects();

        if (emptyDatabase) {
            ensureDemoUser("teacher", "teacher@school.com", "Teacher", Role.TEACHER);
            ensureDemoUser("student", "student@school.com", "Student", Role.STUDENT);
            System.out.println("Demo users created: director, teacher, student (password: password)");
        }
    }

    private void ensureDemoUser(String username, String email, String fullName, Role role) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password"));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        userRepository.save(user);
    }

    private void ensureSchoolClasses() {
        for (int grade = 5; grade <= 12; grade++) {
            ensureSchoolClass(grade + "A", grade);
            ensureSchoolClass(grade + "B", grade);
        }
    }

    private void ensureSchoolClass(String name, int year) {
        if (schoolClassRepository.existsByName(name)) {
            return;
        }

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(name);
        schoolClass.setYear(year);
        schoolClassRepository.save(schoolClass);
    }

    private void ensureSubjects() {
        ensureSubject("Mathematics", "Algebra, geometry and applied math");
        ensureSubject("Russian Language", "Grammar, spelling and writing");
        ensureSubject("Kyrgyz Language", "Grammar, reading and writing");
        ensureSubject("Literature", "Reading, analysis and essays");
        ensureSubject("History", "World and local history");
        ensureSubject("Geography", "Countries, maps and natural systems");
        ensureSubject("Biology", "Living organisms and human biology");
        ensureSubject("Chemistry", "Substances, reactions and laboratory work");
        ensureSubject("Physics", "Mechanics, electricity and natural laws");
        ensureSubject("Computer Science", "Programming and digital literacy");
        ensureSubject("English", "Reading, speaking and grammar");
        ensureSubject("Physical Education", "Sports and health");
    }

    private void ensureSubject(String name, String description) {
        if (subjectRepository.existsByName(name)) {
            return;
        }

        Subject subject = new Subject();
        subject.setName(name);
        subject.setDescription(description);
        subjectRepository.save(subject);
    }
}
