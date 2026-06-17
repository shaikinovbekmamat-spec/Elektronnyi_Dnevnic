package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateTestRequest;
import com.example.taskmanager.dto.SubmitTestRequest;
import com.example.taskmanager.dto.TestDto;
import com.example.taskmanager.dto.TestResultDto;
import java.util.List;

public interface TestingService {
    TestDto createTest(CreateTestRequest request);
    List<TestDto> getTestsByClass(Long classId);
    TestResultDto submitTest(Long testId, SubmitTestRequest request);
    List<TestResultDto> getResultsByStudent(Long studentId);
}
