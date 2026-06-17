package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateScheduleRequest;
import com.example.taskmanager.dto.ScheduleDto;
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
        var schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Класс не найден"));

        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));

        var teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Учитель не найден"));

        Schedule schedule = new Schedule();
        schedule.setSchoolClass(schoolClass);
        schedule.setSubject(subject);
        schedule.setTeacher(teacher);
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setRoom(request.getRoom());

        return toDto(scheduleRepository.save(schedule));
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
    public List<ScheduleDto> getScheduleByClassAndDay(Long classId, String dayOfWeek) {
        return scheduleRepository.findBySchoolClassIdAndDayOfWeek(classId, dayOfWeek)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    private ScheduleDto toDto(Schedule schedule) {
        ScheduleDto dto = new ScheduleDto();
        dto.setId(schedule.getId());
        dto.setClassName(schedule.getSchoolClass().getName());
        dto.setSubjectName(schedule.getSubject().getName());
        dto.setTeacherName(schedule.getTeacher().getFullName());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setRoom(schedule.getRoom());
        return dto;
    }
}