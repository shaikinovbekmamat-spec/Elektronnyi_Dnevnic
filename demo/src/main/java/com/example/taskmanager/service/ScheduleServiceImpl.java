package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateScheduleRequest;
import com.example.taskmanager.dto.ScheduleDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.Schedule;
import com.example.taskmanager.repository.SchoolClassRepository;
import com.example.taskmanager.repository.ScheduleRepository;
import com.example.taskmanager.repository.SubjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SchoolClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Override
    public ScheduleDto createSchedule(CreateScheduleRequest request) {
        Schedule schedule = new Schedule();
        applyScheduleRequest(schedule, request);
        return toDto(scheduleRepository.save(schedule));
    }

    @Override
    public ScheduleDto updateSchedule(Long id, CreateScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        applyScheduleRequest(schedule, request);
        return toDto(scheduleRepository.save(schedule));
    }

    @Override
    public List<ScheduleDto> getAllSchedules() {
        return scheduleRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDto> getScheduleByClass(Long classId) {
        return scheduleRepository.findBySchoolClassId(classId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDto> getScheduleByTeacher(Long teacherId) {
        return scheduleRepository.findByTeacherId(teacherId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDto> getScheduleByTeacherAndSubject(Long teacherId, Long subjectId) {
        return scheduleRepository.findByTeacherIdAndSubjectId(teacherId, subjectId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDto> getScheduleByClassAndDay(Long classId, String dayOfWeek) {
        return scheduleRepository.findBySchoolClassIdAndDayOfWeek(classId, dayOfWeek)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    private void applyScheduleRequest(Schedule schedule, CreateScheduleRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new RuntimeException("Start time must be before end time");
        }

        var schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        var teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("Only teachers can be assigned to lessons");
        }
        if (teacher.getTeacherSubject() == null) {
            throw new RuntimeException("Teacher has no assigned subject");
        }
        if (!teacher.getTeacherSubject().getId().equals(subject.getId())) {
            throw new RuntimeException("Teacher can only be assigned to their subject");
        }

        validateTeacherIsFree(schedule.getId(), teacher.getId(), request);

        schedule.setSchoolClass(schoolClass);
        schedule.setSubject(subject);
        schedule.setTeacher(teacher);
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setRoom(request.getRoom());
    }

    private void validateTeacherIsFree(Long currentScheduleId, Long teacherId, CreateScheduleRequest request) {
        boolean hasConflict = scheduleRepository.findByTeacherIdAndDayOfWeek(teacherId, request.getDayOfWeek())
                .stream()
                .filter(schedule -> currentScheduleId == null || !schedule.getId().equals(currentScheduleId))
                .anyMatch(schedule -> request.getStartTime().isBefore(schedule.getEndTime())
                        && request.getEndTime().isAfter(schedule.getStartTime()));

        if (hasConflict) {
            throw new RuntimeException("Teacher is already busy at this time");
        }
    }

    private ScheduleDto toDto(Schedule schedule) {
        ScheduleDto dto = new ScheduleDto();
        dto.setId(schedule.getId());
        dto.setClassId(schedule.getSchoolClass().getId());
        dto.setClassName(schedule.getSchoolClass().getName());
        dto.setSubjectId(schedule.getSubject().getId());
        dto.setSubjectName(schedule.getSubject().getName());
        dto.setTeacherId(schedule.getTeacher().getId());
        dto.setTeacherName(schedule.getTeacher().getFullName());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setRoom(schedule.getRoom());
        return dto;
    }
}
