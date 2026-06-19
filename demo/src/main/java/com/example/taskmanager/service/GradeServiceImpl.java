package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateGradeRequest;
import com.example.taskmanager.dto.GradeDto;
import com.example.taskmanager.model.Grade;
import com.example.taskmanager.model.GradeType;
import com.example.taskmanager.model.QuarterGradeAccess;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.GradeRepository;
import com.example.taskmanager.repository.QuarterGradeAccessRepository;
import com.example.taskmanager.repository.SubjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final QuarterGradeAccessRepository quarterGradeAccessRepository;

    @Override
    @Transactional
    public GradeDto createGrade(CreateGradeRequest request, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        GradeType gradeType = normalizeType(request.getGradeType());
        Integer quarter = validateQuarter(request.getQuarter());
        LocalDate gradeDate = request.getDate() != null ? request.getDate() : LocalDate.now();

        validateGradeValue(request.getValue());
        validateCurrentDate(gradeDate);

        if (teacher.getRole() == Role.TEACHER) {
            validateTeacherSubject(teacher, subject.getId());
        }

        if (gradeType == GradeType.QUARTER_FINAL) {
            throw new RuntimeException("Quarter final grade is calculated automatically");
        }

        Grade grade;
        if (gradeType == GradeType.FINAL_CONTROL) {
            if (!isQuarterOpen(quarter)) {
                throw new RuntimeException("Quarter grading is closed");
            }
            grade = gradeRepository.findFirstByStudentIdAndSubjectIdAndQuarterAndGradeType(
                            student.getId(), subject.getId(), quarter, GradeType.FINAL_CONTROL)
                    .orElseGet(Grade::new);
            if (grade.getId() != null) {
                validateEditableToday(grade);
            }
        } else {
            grade = new Grade();
        }

        grade.setStudent(student);
        grade.setTeacher(teacher);
        grade.setSubject(subject);
        grade.setValue(request.getValue());
        grade.setComment(request.getComment());
        grade.setDate(gradeDate);
        grade.setQuarter(quarter);
        grade.setGradeType(gradeType);

        Grade saved = gradeRepository.save(grade);
        recalculateQuarterFinal(student.getId(), subject.getId(), quarter);
        return toDto(saved);
    }

    @Override
    @Transactional
    public GradeDto updateGrade(Long id, CreateGradeRequest request, String teacherUsername) {
        User currentUser = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (effectiveType(grade) == GradeType.QUARTER_FINAL) {
            throw new RuntimeException("Quarter final grade cannot be edited manually");
        }
        validateEditableToday(grade);
        validateGradeValue(request.getValue());

        if (currentUser.getRole() == Role.TEACHER) {
            if (!grade.getTeacher().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Teacher can edit only own grades");
            }
            validateTeacherSubject(currentUser, subject.getId());
        }

        Integer quarter = validateQuarter(request.getQuarter() != null ? request.getQuarter() : grade.getQuarter());
        GradeType gradeType = normalizeType(request.getGradeType() != null ? request.getGradeType() : grade.getGradeType());
        if (gradeType == GradeType.FINAL_CONTROL && !isQuarterOpen(quarter)) {
            throw new RuntimeException("Quarter grading is closed");
        }
        if (gradeType == GradeType.QUARTER_FINAL) {
            throw new RuntimeException("Quarter final grade cannot be edited manually");
        }

        grade.setSubject(subject);
        grade.setValue(request.getValue());
        grade.setComment(request.getComment());
        grade.setQuarter(quarter);
        grade.setGradeType(gradeType);
        grade.setDate(request.getDate() != null ? request.getDate() : grade.getDate());
        validateCurrentDate(grade.getDate());

        Grade saved = gradeRepository.save(grade);
        recalculateQuarterFinal(grade.getStudent().getId(), subject.getId(), quarter);
        return toDto(saved);
    }

    @Override
    public List<GradeDto> getGradesByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeDto> getGradesByClass(Long classId) {
        return gradeRepository.findByStudentStudentClassId(classId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeDto> getGradesByClassAndSubject(Long classId, Long subjectId) {
        return gradeRepository.findByStudentStudentClassIdAndSubjectId(classId, subjectId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeDto> getGradesByClassSubjectAndQuarter(Long classId, Long subjectId, Integer quarter) {
        return gradeRepository.findByStudentStudentClassIdAndSubjectIdAndQuarter(classId, subjectId, validateQuarter(quarter))
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
    public boolean isQuarterOpen(Integer quarter) {
        return quarterGradeAccessRepository.findById(validateQuarter(quarter))
                .map(QuarterGradeAccess::isOpen)
                .orElse(false);
    }

    @Override
    public void setQuarterAccess(Integer quarter, boolean open) {
        QuarterGradeAccess access = quarterGradeAccessRepository.findById(validateQuarter(quarter))
                .orElseGet(() -> new QuarterGradeAccess(validateQuarter(quarter), false));
        access.setOpen(open);
        quarterGradeAccessRepository.save(access);
    }

    @Override
    public void deleteGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        if (effectiveType(grade) == GradeType.FINAL_CONTROL && !isQuarterOpen(grade.getQuarter())) {
            throw new RuntimeException("Quarter grading is closed");
        }
        validateEditableToday(grade);
        if (effectiveType(grade) == GradeType.QUARTER_FINAL) {
            throw new RuntimeException("Quarter final grade cannot be deleted manually");
        }
        gradeRepository.deleteById(id);
        recalculateQuarterFinal(grade.getStudent().getId(), grade.getSubject().getId(), grade.getQuarter());
    }

    private void recalculateQuarterFinal(Long studentId, Long subjectId, Integer quarter) {
        if (quarter == null) {
            return;
        }
        Grade finalControl = gradeRepository.findFirstByStudentIdAndSubjectIdAndQuarterAndGradeType(
                studentId, subjectId, quarter, GradeType.FINAL_CONTROL).orElse(null);
        if (finalControl == null) {
            return;
        }

        Double regularAverage = gradeRepository.findRegularAverageByStudentSubjectAndQuarter(studentId, subjectId, quarter);
        double baseAverage = regularAverage != null ? regularAverage : finalControl.getValue();
        int quarterValue = (int) Math.round((baseAverage + finalControl.getValue()) / 2.0);

        Grade quarterFinal = gradeRepository.findFirstByStudentIdAndSubjectIdAndQuarterAndGradeType(
                        studentId, subjectId, quarter, GradeType.QUARTER_FINAL)
                .orElseGet(Grade::new);
        quarterFinal.setStudent(finalControl.getStudent());
        quarterFinal.setTeacher(finalControl.getTeacher());
        quarterFinal.setSubject(finalControl.getSubject());
        quarterFinal.setQuarter(quarter);
        quarterFinal.setGradeType(GradeType.QUARTER_FINAL);
        quarterFinal.setValue(quarterValue);
        quarterFinal.setComment("Итог за четверть");
        quarterFinal.setDate(LocalDate.now());
        gradeRepository.save(quarterFinal);
    }

    private GradeType normalizeType(GradeType gradeType) {
        return gradeType != null ? gradeType : GradeType.REGULAR;
    }

    private GradeType effectiveType(Grade grade) {
        return grade.getGradeType() != null ? grade.getGradeType() : GradeType.REGULAR;
    }

    private Integer validateQuarter(Integer quarter) {
        if (quarter == null || quarter < 1 || quarter > 4) {
            throw new RuntimeException("Quarter must be from 1 to 4");
        }
        return quarter;
    }

    private void validateGradeValue(Integer value) {
        if (value == null || value < 1 || value > 5) {
            throw new RuntimeException("Grade must be from 1 to 5");
        }
    }

    private void validateCurrentDate(LocalDate date) {
        if (!LocalDate.now().equals(date)) {
            throw new RuntimeException("Grades can be created or edited only on the current date");
        }
    }

    private void validateEditableToday(Grade grade) {
        if (!LocalDate.now().equals(grade.getDate())) {
            throw new RuntimeException("Grade can be edited only on the day it was created");
        }
    }

    private void validateTeacherSubject(User teacher, Long subjectId) {
        if (teacher.getTeacherSubject() == null) {
            throw new RuntimeException("Teacher has no assigned subject");
        }
        if (!teacher.getTeacherSubject().getId().equals(subjectId)) {
            throw new RuntimeException("Teacher can work only with own subject");
        }
    }

    private GradeDto toDto(Grade grade) {
        GradeDto dto = new GradeDto();
        dto.setId(grade.getId());
        dto.setStudentId(grade.getStudent().getId());
        dto.setStudentName(grade.getStudent().getFullName());
        dto.setTeacherId(grade.getTeacher().getId());
        dto.setTeacherName(grade.getTeacher().getFullName());
        dto.setSubjectId(grade.getSubject().getId());
        dto.setSubjectName(grade.getSubject().getName());
        dto.setValue(grade.getValue());
        dto.setComment(grade.getComment());
        dto.setDate(grade.getDate());
        dto.setQuarter(grade.getQuarter());
        dto.setGradeType(effectiveType(grade));
        boolean editableToday = effectiveType(grade) != GradeType.QUARTER_FINAL && LocalDate.now().equals(grade.getDate());
        if (effectiveType(grade) == GradeType.FINAL_CONTROL) {
            editableToday = editableToday && isQuarterOpen(grade.getQuarter());
        }
        dto.setEditableToday(editableToday);
        return dto;
    }
}
