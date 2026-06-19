package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateHomeworkRequest;
import com.example.taskmanager.dto.HomeworkDto;
import com.example.taskmanager.model.Homework;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.Subject;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.HomeworkRepository;
import com.example.taskmanager.repository.SchoolClassRepository;
import com.example.taskmanager.repository.SubjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final FileService fileService;

    @Override
    public HomeworkDto createHomework(CreateHomeworkRequest request, MultipartFile file) throws IOException {
        validateDueDate(request);
        Homework homework = new Homework();
        applyHomeworkRequest(homework, request, file, false);
        return toDto(homeworkRepository.save(homework));
    }

    @Override
    public HomeworkDto updateHomework(Long id, CreateHomeworkRequest request, MultipartFile file) throws IOException {
        validateDueDate(request);
        Homework homework = homeworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Homework not found"));
        applyHomeworkRequest(homework, request, file, true);
        return toDto(homeworkRepository.save(homework));
    }

    @Override
    public List<HomeworkDto> getAllHomework() {
        return homeworkRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HomeworkDto> getHomeworkByClass(Long classId) {
        return homeworkRepository.findBySchoolClassId(classId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HomeworkDto> getHomeworkByTeacher(Long teacherId) {
        return homeworkRepository.findByTeacherId(teacherId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteHomework(Long id) {
        homeworkRepository.findById(id).ifPresent(h -> {
            if (h.getFilePath() != null) {
                fileService.deleteFile(h.getFilePath());
            }
            homeworkRepository.deleteById(id);
        });
    }

    private void applyHomeworkRequest(
            Homework homework,
            CreateHomeworkRequest request,
            MultipartFile file,
            boolean editing) throws IOException {
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        var schoolClass = schoolClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (editing && teacher.getRole() == Role.TEACHER && !homework.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Teacher can edit only own homework");
        }
        validateTeacherSubject(teacher, subject.getId());

        homework.setTeacher(teacher);
        homework.setSubject(subject);
        homework.setSchoolClass(schoolClass);
        homework.setTitle(request.getTitle());
        homework.setDescription(request.getDescription());
        homework.setDueDate(request.getDueDate());

        if (file != null && !file.isEmpty()) {
            if (homework.getFilePath() != null) {
                fileService.deleteFile(homework.getFilePath());
            }
            homework.setFilePath(fileService.saveFile(file, "homework"));
        }
    }

    private void validateDueDate(CreateHomeworkRequest request) {
        if (request.getDueDate() == null || !request.getDueDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Due date must be tomorrow or later");
        }
    }

    private void validateTeacherSubject(User teacher, Long subjectId) {
        if (teacher.getRole() == Role.TEACHER) {
            if (teacher.getTeacherSubject() == null) {
                throw new RuntimeException("Teacher has no assigned subject");
            }
            if (!teacher.getTeacherSubject().getId().equals(subjectId)) {
                throw new RuntimeException("Teacher can assign homework only for own subject");
            }
        }
    }

    private HomeworkDto toDto(Homework homework) {
        HomeworkDto dto = new HomeworkDto();
        dto.setId(homework.getId());
        dto.setSubjectId(homework.getSubject().getId());
        dto.setSubjectName(homework.getSubject().getName());
        dto.setClassId(homework.getSchoolClass().getId());
        dto.setClassName(homework.getSchoolClass().getName());
        dto.setTeacherId(homework.getTeacher().getId());
        dto.setTeacherName(homework.getTeacher().getFullName());
        dto.setTitle(homework.getTitle());
        dto.setDescription(homework.getDescription());
        dto.setDueDate(homework.getDueDate());
        dto.setFilePath(homework.getFilePath());
        return dto;
    }
}
