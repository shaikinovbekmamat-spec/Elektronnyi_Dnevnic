package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateGradeRequest;

import com.example.taskmanager.dto.GradeDto;
import com.example.taskmanager.model.Grade;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.GradeRepository;
import com.example.taskmanager.repository.SubjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public GradeDto createGrade(CreateGradeRequest request, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Учитель не найден"));

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Ученик не найден"));

        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setTeacher(teacher);
        grade.setSubject(subject);
        grade.setValue(request.getValue());
        grade.setComment(request.getComment());
        grade.setDate(request.getDate());

        return toDto(gradeRepository.save(grade));
    }

    @Override
    public List<GradeDto> getGradesByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageGrade(Long studentId) {
        return gradeRepository.findAverageGradeByStudentId(studentId);
    }

    @Override
    public Double getAverageGradeBySubject(Long studentId, Long subjectId) {
        return gradeRepository.findAverageGradeByStudentIdAndSubjectId(studentId, subjectId);
    }

    @Override
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    private GradeDto toDto(Grade grade) {
        GradeDto dto = new GradeDto();
        dto.setId(grade.getId());
        dto.setStudentName(grade.getStudent().getFullName());
        dto.setTeacherName(grade.getTeacher().getFullName());
        dto.setSubjectName(grade.getSubject().getName());
        dto.setValue(grade.getValue());
        dto.setComment(grade.getComment());
        dto.setDate(grade.getDate());
        return dto;
    }
}