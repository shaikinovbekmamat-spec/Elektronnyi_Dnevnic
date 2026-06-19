package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateUserRequest;
import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;
    private final AnnouncementRepository announcementRepository;
    private final ScheduleRepository scheduleRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkSubmissionRepository homeworkSubmissionRepository;
    private final TestRepository testRepository;
    private final TestResultRepository testResultRepository;
    private final VideoLessonRepository videoLessonRepository;
    private final TeacherRatingRepository teacherRatingRepository;
    private final FileService fileService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto);
    }

    @Override
    public UserDto createUser(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        if (request.getRole() == Role.STUDENT && request.getStudentClassId() != null) {
            user.setStudentClass(schoolClassRepository.findById(request.getStudentClassId())
                    .orElseThrow(() -> new RuntimeException("Класс не найден")));
        }
        if (request.getRole() == Role.TEACHER && request.getTeacherSubjectId() != null) {
            user.setTeacherSubject(subjectRepository.findById(request.getTeacherSubjectId())
                    .orElseThrow(() -> new RuntimeException("Предмет не найден")));
        }
        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        homeworkSubmissionRepository.findByStudentIdOrTeacherId(id, id).forEach(submission -> {
            if (submission.getFilePath() != null) {
                fileService.deleteFile(submission.getFilePath());
            }
            homeworkSubmissionRepository.delete(submission);
        });

        homeworkRepository.findByTeacherId(id).forEach(homework -> {
            if (homework.getFilePath() != null) {
                fileService.deleteFile(homework.getFilePath());
            }
            homeworkRepository.delete(homework);
        });

        if (user.getRole() == Role.STUDENT) {
            attendanceRepository.deleteByStudentId(id);
            testResultRepository.deleteByStudentId(id);
        }

        if (user.getRole() == Role.TEACHER) {
            attendanceRepository.deleteByScheduleTeacherId(id);
            scheduleRepository.deleteByTeacherId(id);
            testResultRepository.deleteByTestTeacherId(id);
            testRepository.deleteByTeacherId(id);
            videoLessonRepository.deleteByTeacherId(id);
        }

        gradeRepository.deleteByStudentIdOrTeacherId(id, id);
        teacherRatingRepository.deleteByStudentIdOrTeacherId(id, id);
        announcementRepository.deleteByAuthorId(id);
        schoolClassRepository.findAll().stream()
                .filter(schoolClass -> schoolClass.getClassTeacher() != null
                        && schoolClass.getClassTeacher().getId().equals(id))
                .forEach(schoolClass -> {
                    schoolClass.setClassTeacher(null);
                    schoolClassRepository.save(schoolClass);
                });
        userRepository.deleteById(id);
    }

    // Конвертация переехала сюда из контроллера
    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStudentClass() != null ? user.getStudentClass().getId() : null,
                user.getStudentClass() != null ? user.getStudentClass().getName() : null,
                user.getTeacherSubject() != null ? user.getTeacherSubject().getId() : null,
                user.getTeacherSubject() != null ? user.getTeacherSubject().getName() : null
        );
    }
}
