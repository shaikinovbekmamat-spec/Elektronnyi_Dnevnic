package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateHomeworkSubmissionRequest;
import com.example.taskmanager.dto.HomeworkSubmissionDto;
import com.example.taskmanager.model.Homework;
import com.example.taskmanager.model.HomeworkSubmission;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.Subject;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.HomeworkRepository;
import com.example.taskmanager.repository.HomeworkSubmissionRepository;
import com.example.taskmanager.repository.SubjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkSubmissionServiceImpl implements HomeworkSubmissionService {

    private final HomeworkSubmissionRepository submissionRepository;
    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final FileService fileService;

    @Override
    public HomeworkSubmissionDto submitHomework(
            CreateHomeworkSubmissionRequest request,
            MultipartFile file,
            String studentUsername) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Solution file is required");
        }

        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can submit homework");
        }
        if (student.getStudentClass() == null) {
            throw new RuntimeException("Student has no class");
        }

        Homework homework = homeworkRepository.findById(request.getHomeworkId())
                .orElseThrow(() -> new RuntimeException("Homework not found"));
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!homework.getSchoolClass().getId().equals(student.getStudentClass().getId())) {
            throw new RuntimeException("Student can submit only own class homework");
        }
        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("Selected user is not a teacher");
        }
        if (teacher.getTeacherSubject() == null || !teacher.getTeacherSubject().getId().equals(subject.getId())) {
            throw new RuntimeException("Teacher subject does not match selected subject");
        }
        if (!homework.getSubject().getId().equals(subject.getId())
                || !homework.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Teacher and subject must match selected homework");
        }

        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomework(homework);
        submission.setStudent(student);
        submission.setTeacher(teacher);
        submission.setSubject(subject);
        submission.setComment(request.getComment());
        submission.setFilePath(fileService.saveFile(file, "submissions"));
        submission.setSubmittedAt(LocalDateTime.now());

        return toDto(submissionRepository.save(submission));
    }

    @Override
    public List<HomeworkSubmissionDto> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentIdOrderBySubmittedAtDesc(studentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HomeworkSubmissionDto> getSubmissionsByTeacher(Long teacherId) {
        return submissionRepository.findByTeacherIdOrderBySubmittedAtDesc(teacherId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HomeworkSubmissionDto> getAllSubmissions() {
        return submissionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private HomeworkSubmissionDto toDto(HomeworkSubmission submission) {
        HomeworkSubmissionDto dto = new HomeworkSubmissionDto();
        dto.setId(submission.getId());
        dto.setHomeworkId(submission.getHomework().getId());
        dto.setHomeworkTitle(submission.getHomework().getTitle());
        dto.setStudentId(submission.getStudent().getId());
        dto.setStudentName(submission.getStudent().getFullName());
        dto.setTeacherId(submission.getTeacher().getId());
        dto.setTeacherName(submission.getTeacher().getFullName());
        dto.setSubjectId(submission.getSubject().getId());
        dto.setSubjectName(submission.getSubject().getName());
        dto.setClassName(submission.getHomework().getSchoolClass().getName());
        dto.setComment(submission.getComment());
        dto.setFilePath(submission.getFilePath());
        dto.setSubmittedAt(submission.getSubmittedAt());
        return dto;
    }
}
