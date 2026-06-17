package com.example.taskmanager.service;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestingServiceImpl implements TestingService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;

    @Override
    public TestDto createTest(CreateTestRequest request) {
        var teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Учитель не найден"));
        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));
        var schoolClass = schoolClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Класс не найден"));

        Test test = new Test();
        test.setTitle(request.getTitle());
        test.setTeacher(teacher);
        test.setSubject(subject);
        test.setSchoolClass(schoolClass);
        test.setDuration(request.getDuration());
        test.setDeadline(request.getDeadline());

        test = testRepository.save(test);

        Test finalTest = test;
        List<Question> questions = request.getQuestions().stream().map(qDto -> {
            Question question = new Question();
            question.setTest(finalTest);
            question.setText(qDto.getText());
            question.setType(qDto.getType());
            question.setCorrectAnswer(qDto.getCorrectAnswer());
            return question;
        }).collect(Collectors.toList());

        questionRepository.saveAll(questions);
        test.setQuestions(questions);

        return toDto(test);
    }

    @Override
    public List<TestDto> getTestsByClass(Long classId) {
        return testRepository.findBySchoolClassId(classId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TestResultDto submitTest(Long testId, SubmitTestRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Ученик не найден"));

        List<Question> questions = test.getQuestions();
        Map<Long, String> studentAnswers = request.getAnswers();

        long correctCount = 0;
        for (Question q : questions) {
            String studentAnswer = studentAnswers.get(q.getId());
            if (studentAnswer != null && studentAnswer.trim().equalsIgnoreCase(q.getCorrectAnswer().trim())) {
                correctCount++;
            }
        }

        int score = (int) ((correctCount * 100) / questions.size());
        int grade = calculateGrade(score);

        TestResult result = new TestResult();
        result.setTest(test);
        result.setStudent(student);
        result.setScore(score);
        result.setGrade(grade);
        result.setCompletedAt(LocalDateTime.now());

        return toResultDto(testResultRepository.save(result));
    }

    @Override
    public List<TestResultDto> getResultsByStudent(Long studentId) {
        return testResultRepository.findByStudentId(studentId).stream()
                .map(this::toResultDto)
                .collect(Collectors.toList());
    }

    private int calculateGrade(int score) {
        if (score >= 90) return 5;
        if (score >= 75) return 4;
        if (score >= 60) return 3;
        if (score >= 40) return 2;
        return 1;
    }

    private TestDto toDto(Test test) {
        TestDto dto = new TestDto();
        dto.setId(test.getId());
        dto.setTitle(test.getTitle());
        dto.setSubjectName(test.getSubject().getName());
        dto.setClassName(test.getSchoolClass().getName());
        dto.setTeacherName(test.getTeacher().getFullName());
        dto.setDuration(test.getDuration());
        dto.setDeadline(test.getDeadline().toString());
        dto.setQuestions(test.getQuestions().stream().map(q -> {
            QuestionDto qDto = new QuestionDto();
            qDto.setId(q.getId());
            qDto.setText(q.getText());
            qDto.setType(q.getType());
            // Hide correct answer when sending test to student? 
            // In a real app we would have a separate DTO for taking the test.
            return qDto;
        }).collect(Collectors.toList()));
        return dto;
    }

    private TestResultDto toResultDto(TestResult result) {
        TestResultDto dto = new TestResultDto();
        dto.setId(result.getId());
        dto.setTestId(result.getTest().getId());
        dto.setTestTitle(result.getTest().getTitle());
        dto.setStudentName(result.getStudent().getFullName());
        dto.setScore(result.getScore());
        dto.setGrade(result.getGrade());
        dto.setCompletedAt(result.getCompletedAt());
        return dto;
    }
}
