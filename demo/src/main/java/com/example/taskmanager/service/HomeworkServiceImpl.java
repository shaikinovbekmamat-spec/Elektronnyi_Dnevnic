package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateHomeworkRequest;
import com.example.taskmanager.dto.HomeworkDto;
import com.example.taskmanager.model.Homework;
import com.example.taskmanager.repository.HomeworkRepository;
import com.example.taskmanager.repository.SchoolClassRepository;
import com.example.taskmanager.repository.SubjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
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
        var teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Учитель не найден"));
        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));
        var schoolClass = schoolClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Класс не найден"));

        Homework homework = new Homework();
        homework.setTeacher(teacher);
        homework.setSubject(subject);
        homework.setSchoolClass(schoolClass);
        homework.setTitle(request.getTitle());
        homework.setDescription(request.getDescription());
        homework.setDueDate(request.getDueDate());

        if (file != null && !file.isEmpty()) {
            String filePath = fileService.saveFile(file, "homework");
            homework.setFilePath(filePath);
        }

        return toDto(homeworkRepository.save(homework));
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

    private HomeworkDto toDto(Homework homework) {
        HomeworkDto dto = new HomeworkDto();
        dto.setId(homework.getId());
        dto.setTitle(homework.getTitle());
        dto.setDescription(homework.getDescription());
        dto.setDueDate(homework.getDueDate());
        dto.setTeacherName(homework.getTeacher().getFullName());
        dto.setSubjectName(homework.getSubject().getName());
        dto.setClassName(homework.getSchoolClass().getName());
        dto.setFilePath(homework.getFilePath());
        return dto;
    }
}
