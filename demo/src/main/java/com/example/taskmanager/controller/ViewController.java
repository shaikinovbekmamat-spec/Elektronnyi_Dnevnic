package com.example.taskmanager.controller;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import com.example.taskmanager.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final GradeService gradeService;
    private final HomeworkService homeworkService;
    private final HomeworkSubmissionService homeworkSubmissionService;
    private final AnnouncementService announcementService;
    private final ScheduleService scheduleService;
    private final TeacherRatingService teacherRatingService;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;
    private final ScheduleRepository scheduleRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkSubmissionRepository homeworkSubmissionRepository;
    private final TestRepository testRepository;
    private final TestResultRepository testResultRepository;
    private final VideoLessonRepository videoLessonRepository;
    private final AnnouncementRepository announcementRepository;
    private final FileService fileService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/schedule")
    public String schedule(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Расписание");
        
        if (user != null) {
            if (user.getRole() == Role.STUDENT) {
                if (user.getStudentClass() != null) {
                    model.addAttribute("schedule", scheduleService.getScheduleByClass(user.getStudentClass().getId()));
                } else {
                    model.addAttribute("schedule", List.of());
                }
            } else if (user.getRole() == Role.DIRECTOR) {
                model.addAttribute("schedule", scheduleService.getAllSchedules());
                model.addAttribute("classes", schoolClassRepository.findAll());
                model.addAttribute("subjects", subjectRepository.findAll());
                model.addAttribute("teachers", userRepository.findByRole(Role.TEACHER));
            } else {
                if (user.getTeacherSubject() != null) {
                    model.addAttribute("schedule", scheduleService.getScheduleByTeacherAndSubject(
                            user.getId(), user.getTeacherSubject().getId()));
                } else {
                    model.addAttribute("schedule", List.of());
                }
            }
        }
        return "schedule";
    }

    @PostMapping("/schedule/add")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String addSchedule(@RequestParam Long classId,
                               @RequestParam Long subjectId,
                               @RequestParam Long teacherId,
                               String dayOfWeek,
                               String startTime,
                               String endTime,
                               String classroom,
                               Principal principal) {
        CreateScheduleRequest request = new CreateScheduleRequest();
        request.setClassId(classId);
        request.setSubjectId(subjectId);
        request.setDayOfWeek(dayOfWeek);
        request.setStartTime(java.time.LocalTime.parse(startTime));
        request.setEndTime(java.time.LocalTime.parse(endTime));
        request.setRoom(classroom);
        request.setTeacherId(teacherId);
        
        try {
            scheduleService.createSchedule(request);
            return "redirect:/schedule?success";
        } catch (RuntimeException e) {
            return "redirect:/schedule?error";
        }
    }

    @PostMapping("/schedule/{id}/edit")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String editSchedule(@PathVariable Long id,
                               @RequestParam Long classId,
                               @RequestParam Long subjectId,
                               @RequestParam Long teacherId,
                               String dayOfWeek,
                               String startTime,
                               String endTime,
                               String classroom) {
        CreateScheduleRequest request = new CreateScheduleRequest();
        request.setClassId(classId);
        request.setSubjectId(subjectId);
        request.setTeacherId(teacherId);
        request.setDayOfWeek(dayOfWeek);
        request.setStartTime(java.time.LocalTime.parse(startTime));
        request.setEndTime(java.time.LocalTime.parse(endTime));
        request.setRoom(classroom);

        try {
            scheduleService.updateSchedule(id, request);
            return "redirect:/schedule?success";
        } catch (RuntimeException e) {
            return "redirect:/schedule?error";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userRequest", new CreateUserRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute CreateUserRequest request, Model model) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setRole(request.getRole());
            userRepository.save(user);
            return "redirect:/login?registered";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            model.addAttribute("error", "Пользователь с таким логином или email уже существует");
            model.addAttribute("userRequest", request);
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка при регистрации");
            model.addAttribute("userRequest", request);
            return "register";
        }
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Панель управления");
        model.addAttribute("user", user);
        
        if (user != null) {
            if (user.getRole() == Role.STUDENT) {
                model.addAttribute("averageGrade", gradeService.getAverageGrade(user.getId()));
            }
            model.addAttribute("announcements", getVisibleAnnouncements(user));
        }
        
        return "dashboard";
    }

    @PostMapping("/teacher-ratings/{teacherId}")
    @PreAuthorize("hasRole('STUDENT')")
    public String rateTeacher(@PathVariable Long teacherId,
                              @RequestParam Integer rating,
                              Principal principal) {
        try {
            teacherRatingService.rateTeacher(principal.getName(), teacherId, rating);
            return "redirect:/dashboard?ratingSuccess";
        } catch (RuntimeException e) {
            return "redirect:/dashboard?ratingError";
        }
    }

    @GetMapping("/teacher-ratings")
    public String teacherRatings(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Оценка учителей");
        if (user != null && user.getRole() == Role.STUDENT) {
            model.addAttribute("teacherRatings", teacherRatingService.getTeacherRatings(user.getId()));
        } else {
            model.addAttribute("teacherRatings", teacherRatingService.getTeacherRatings(null));
        }
        return "teacher-ratings";
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'DIRECTOR')")
    public String attendance(Model model,
                             Principal principal,
                             @RequestParam(required = false) Long classId) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Журнал посещаемости");
        model.addAttribute("today", LocalDate.now());

        if (user == null) {
            return "attendance";
        }

        if (user.getRole() == Role.STUDENT) {
            model.addAttribute("studentAttendance", attendanceService.getAttendanceByStudent(user.getId()));
            return "attendance";
        }

        if (user.getRole() != Role.TEACHER || user.getTeacherSubject() == null) {
            model.addAttribute("availableClasses", List.of());
            model.addAttribute("attendanceLessons", List.of());
            return "attendance";
        }

        Long subjectId = user.getTeacherSubject().getId();
        List<Schedule> teacherSchedules = scheduleRepository.findByTeacherIdAndSubjectId(user.getId(), subjectId);
        List<SchoolClass> availableClasses = schoolClassRepository.findAll();
        model.addAttribute("availableClasses", availableClasses);

        SchoolClass selectedClass = classId != null
                ? schoolClassRepository.findById(classId).orElse(null)
                : availableClasses.stream().findFirst().orElse(null);
        model.addAttribute("selectedClass", selectedClass);

        if (selectedClass == null) {
            model.addAttribute("attendanceLessons", List.of());
            return "attendance";
        }

        String todayDay = LocalDate.now().getDayOfWeek().name();
        List<Schedule> todaySchedules = teacherSchedules.stream()
                .filter(schedule -> schedule.getSchoolClass().getId().equals(selectedClass.getId()))
                .filter(schedule -> schedule.getDayOfWeek() != null && schedule.getDayOfWeek().equalsIgnoreCase(todayDay))
                .collect(Collectors.toList());

        List<AttendanceLessonDto> attendanceLessons = new ArrayList<>();
        List<User> students = userRepository.findByStudentClassId(selectedClass.getId());

        for (Schedule schedule : todaySchedules) {
            Map<Long, Attendance> attendanceMap = attendanceRepository.findByScheduleIdAndDate(schedule.getId(), LocalDate.now())
                    .stream()
                    .collect(Collectors.toMap(a -> a.getStudent().getId(), a -> a, (left, right) -> left));

            AttendanceLessonDto lesson = new AttendanceLessonDto();
            lesson.setScheduleId(schedule.getId());
            lesson.setClassId(selectedClass.getId());
            lesson.setClassName(selectedClass.getName());
            lesson.setSubjectName(schedule.getSubject().getName());
            lesson.setStartTime(schedule.getStartTime());
            lesson.setEndTime(schedule.getEndTime());
            lesson.setRoom(schedule.getRoom());

            List<AttendanceStudentRowDto> rows = new ArrayList<>();
            for (User student : students) {
                Attendance attendance = attendanceMap.get(student.getId());
                AttendanceStudentRowDto row = new AttendanceStudentRowDto();
                row.setAttendanceId(attendance != null ? attendance.getId() : null);
                row.setStudentId(student.getId());
                row.setStudentName(student.getFullName());
                row.setStatus(attendance != null ? attendance.getStatus() : "");
                row.setReason(attendance != null ? attendance.getReason() : "");
                row.setEditableToday(true);
                rows.add(row);
            }
            lesson.setRows(rows);
            attendanceLessons.add(lesson);
        }

        model.addAttribute("attendanceLessons", attendanceLessons);
        model.addAttribute("todayDay", todayDay);
        return "attendance";
    }

    @PostMapping("/attendance/mark")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public String markAttendance(@RequestParam Long studentId,
                                 @RequestParam Long scheduleId,
                                 @RequestParam Long classId,
                                 @RequestParam String status,
                                 Principal principal) {
        MarkAttendanceRequest request = new MarkAttendanceRequest();
        request.setStudentId(studentId);
        request.setScheduleId(scheduleId);
        request.setDate(LocalDate.now());
        request.setStatus(status);

        try {
            attendanceService.markAttendance(request);
            return "redirect:/attendance?classId=" + classId + "&success";
        } catch (RuntimeException e) {
            return "redirect:/attendance?classId=" + classId + "&error";
        }
    }

    @GetMapping("/grades")
    public String grades(Model model, Principal principal) {
        model.addAttribute("quarters", List.of(1, 2, 3, 4));
        addQuarterAccessModel(model);
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Журнал оценок");
        
        if (user != null) {
            if (user.getRole() == Role.STUDENT) {
                model.addAttribute("grades", gradeService.getGradesByStudent(user.getId()));
            } else if (user.getRole() == Role.TEACHER || user.getRole() == Role.DIRECTOR) {
                model.addAttribute("classes", schoolClassRepository.findAll());
                if (user.getRole() == Role.TEACHER) {
                    model.addAttribute("subjects", user.getTeacherSubject() != null
                            ? List.of(user.getTeacherSubject())
                            : List.of());
                } else {
                    model.addAttribute("subjects", subjectRepository.findAll());
                }
            }
        }
        return "grades";
    }

    @GetMapping("/grades/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public String gradesByClass(@PathVariable Long classId, Model model, Principal principal) {
        model.addAttribute("quarters", List.of(1, 2, 3, 4));
        addQuarterAccessModel(model);
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Grade journal");
        model.addAttribute("classes", schoolClassRepository.findAll());
        model.addAttribute("selectedClass", schoolClassRepository.findById(classId).orElse(null));
        model.addAttribute("students", userRepository.findByStudentClassId(classId));

        if (user != null && user.getRole() == Role.TEACHER) {
            model.addAttribute("subjects", user.getTeacherSubject() != null
                    ? List.of(user.getTeacherSubject())
                    : List.of());
            if (user.getTeacherSubject() != null) {
                model.addAttribute("classGrades", gradeService.getGradesByClassAndSubject(classId, user.getTeacherSubject().getId()));
            } else {
                model.addAttribute("classGrades", List.of());
            }
        } else {
            model.addAttribute("subjects", subjectRepository.findAll());
            model.addAttribute("classGrades", gradeService.getGradesByClass(classId));
        }
        return "grades";
    }

    @PostMapping("/grades/add")
    @PreAuthorize("hasRole('TEACHER')")
    public String addGrade(@RequestParam Long studentId, 
                           @RequestParam Long subjectId, 
                           @RequestParam Integer value,
                           @RequestParam Integer quarter,
                           @RequestParam(defaultValue = "REGULAR") GradeType gradeType,
                           @RequestParam String comment,
                           Principal principal) {
        CreateGradeRequest request = new CreateGradeRequest();
        request.setStudentId(studentId);
        request.setSubjectId(subjectId);
        request.setValue(value);
        request.setQuarter(quarter);
        request.setGradeType(gradeType);
        request.setComment(comment);

        try {
            gradeService.createGrade(request, principal.getName());
            User student = userRepository.findById(studentId).orElse(null);
            if (student != null && student.getStudentClass() != null) {
                return "redirect:/grades/class/" + student.getStudentClass().getId() + "?success";
            }
            return "redirect:/grades?success";
        } catch (RuntimeException e) {
            User student = userRepository.findById(studentId).orElse(null);
            if (student != null && student.getStudentClass() != null) {
                return "redirect:/grades/class/" + student.getStudentClass().getId() + "?error";
            }
            return "redirect:/grades?error";
        }
    }

    @PostMapping("/grades/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String editGrade(@PathVariable Long id,
                            @RequestParam Long studentId,
                            @RequestParam Long subjectId,
                            @RequestParam Integer value,
                            @RequestParam Integer quarter,
                            @RequestParam(defaultValue = "REGULAR") GradeType gradeType,
                            @RequestParam String comment,
                            Principal principal) {
        CreateGradeRequest request = new CreateGradeRequest();
        request.setStudentId(studentId);
        request.setSubjectId(subjectId);
        request.setValue(value);
        request.setQuarter(quarter);
        request.setGradeType(gradeType);
        request.setComment(comment);

        try {
            gradeService.updateGrade(id, request, principal.getName());
        } catch (RuntimeException e) {
            User student = userRepository.findById(studentId).orElse(null);
            if (student != null && student.getStudentClass() != null) {
                return "redirect:/grades/class/" + student.getStudentClass().getId() + "?error";
            }
            return "redirect:/grades?error";
        }

        User student = userRepository.findById(studentId).orElse(null);
        if (student != null && student.getStudentClass() != null) {
            return "redirect:/grades/class/" + student.getStudentClass().getId() + "?success";
        }
        return "redirect:/grades?success";
    }

    @PostMapping("/grades/quarter-access")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String setQuarterAccess(@RequestParam Integer quarter,
                                   @RequestParam(defaultValue = "false") boolean open,
                                   @RequestParam(required = false) Long classId) {
        try {
            gradeService.setQuarterAccess(quarter, open);
            if (classId != null) {
                return "redirect:/grades/class/" + classId + "?success";
            }
            return "redirect:/grades?success";
        } catch (RuntimeException e) {
            if (classId != null) {
                return "redirect:/grades/class/" + classId + "?error";
            }
            return "redirect:/grades?error";
        }
    }

    @GetMapping("/homework")
    public String homework(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Домашние задания");
        model.addAttribute("minDueDate", java.time.LocalDate.now().plusDays(1));
        
        if (user != null) {
            if (user.getRole() == Role.STUDENT) {
                if (user.getStudentClass() != null) {
                    model.addAttribute("homeworks", homeworkService.getHomeworkByClass(user.getStudentClass().getId()));
                } else {
                    model.addAttribute("homeworks", List.of());
                }
                model.addAttribute("teachers", userRepository.findByRole(Role.TEACHER));
                model.addAttribute("subjects", subjectRepository.findAll());
                model.addAttribute("submissions", homeworkSubmissionService.getSubmissionsByStudent(user.getId()));
            } else if (user.getRole() == Role.DIRECTOR) {
                model.addAttribute("homeworks", homeworkService.getAllHomework());
                model.addAttribute("classes", schoolClassRepository.findAll());
                model.addAttribute("subjects", subjectRepository.findAll());
                model.addAttribute("submissions", homeworkSubmissionService.getAllSubmissions());
            } else {
                model.addAttribute("homeworks", homeworkService.getHomeworkByTeacher(user.getId()));
                model.addAttribute("classes", schoolClassRepository.findAll());
                model.addAttribute("subjects", user.getTeacherSubject() != null
                        ? List.of(user.getTeacherSubject())
                        : List.of());
                model.addAttribute("submissions", homeworkSubmissionService.getSubmissionsByTeacher(user.getId()));
            }
        }
        return "homework";
    }

    @GetMapping("/announcements")
    public String announcements(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Объявления");
        if (user != null) {
            model.addAttribute("announcements", getVisibleAnnouncements(user));
            model.addAttribute("classes", schoolClassRepository.findAll());
            if (user.getRole() == Role.TEACHER) {
                model.addAttribute("subjects", user.getTeacherSubject() != null
                        ? List.of(user.getTeacherSubject())
                        : List.of());
            } else {
                model.addAttribute("subjects", subjectRepository.findAll());
            }
        }
        return "announcements";
    }

    @PostMapping("/announcements/add")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public String addAnnouncement(@RequestParam String title,
                                  @RequestParam String content,
                                  @RequestParam(required = false) Long classId,
                                  @RequestParam(required = false) Long subjectId,
                                  @RequestParam String type,
                                  Principal principal) {
        CreateAnnouncementRequest request = new CreateAnnouncementRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setClassId(classId);
        request.setSubjectId(subjectId);
        request.setTargetType(type);
        
        announcementService.createAnnouncement(request, principal.getName());
        return "redirect:/announcements?success";
    }

    @PostMapping("/homework/add")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public String addHomework(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String dueDate,
                              @RequestParam Long classId,
                              @RequestParam Long subjectId,
                              @RequestParam(required = false) org.springframework.web.multipart.MultipartFile file,
                              Principal principal) throws java.io.IOException {
        CreateHomeworkRequest request = new CreateHomeworkRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setDueDate(java.time.LocalDate.parse(dueDate));
        request.setSubjectId(subjectId);
        request.setClassId(classId);
        request.setTeacherId(userRepository.findByUsername(principal.getName()).get().getId());
        try {
            homeworkService.createHomework(request, file);
            return "redirect:/homework?success";
        } catch (RuntimeException e) {
            return "redirect:/homework?error";
        }
    }

    @PostMapping("/homework/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public String submitHomework(@RequestParam Long homeworkId,
                                 @RequestParam Long subjectId,
                                 @RequestParam Long teacherId,
                                 @RequestParam(required = false) String comment,
                                 @RequestParam org.springframework.web.multipart.MultipartFile file,
                                 Principal principal) throws java.io.IOException {
        CreateHomeworkSubmissionRequest request = new CreateHomeworkSubmissionRequest();
        request.setHomeworkId(homeworkId);
        request.setSubjectId(subjectId);
        request.setTeacherId(teacherId);
        request.setComment(comment);

        try {
            homeworkSubmissionService.submitHomework(request, file, principal.getName());
            return "redirect:/homework?submitted";
        } catch (RuntimeException e) {
            return "redirect:/homework?submitError";
        }
    }

    @PostMapping("/homework/{id}/edit")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public String editHomework(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam String dueDate,
                               @RequestParam Long classId,
                               @RequestParam Long subjectId,
                               @RequestParam(required = false) org.springframework.web.multipart.MultipartFile file,
                               Principal principal) throws java.io.IOException {
        CreateHomeworkRequest request = new CreateHomeworkRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setDueDate(java.time.LocalDate.parse(dueDate));
        request.setSubjectId(subjectId);
        request.setClassId(classId);
        request.setTeacherId(userRepository.findByUsername(principal.getName()).get().getId());

        try {
            homeworkService.updateHomework(id, request, file);
            return "redirect:/homework?success";
        } catch (RuntimeException e) {
            return "redirect:/homework?error";
        }
    }

    @PostMapping("/schedule/{id}/delete")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/schedule?success";
    }

    @PostMapping("/announcements/{id}/delete")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/announcements?success";
    }

    @GetMapping("/director/users")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String directorUsers(Model model) {
        model.addAttribute("title", "Управление пользователями");
        model.addAttribute("students", userRepository.findByRole(Role.STUDENT));
        model.addAttribute("teachers", userRepository.findByRole(Role.TEACHER));
        model.addAttribute("directors", userRepository.findByRole(Role.DIRECTOR));
        model.addAttribute("classes", schoolClassRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "director-users";
    }

    @PostMapping("/director/users/{id}/delete")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/director/users?success";
        } catch (RuntimeException e) {
            return "redirect:/director/users?deleteError";
        }
    }

    @PostMapping("/director/classes/add")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String addClass(@RequestParam String name, @RequestParam Integer year) {
        if (schoolClassRepository.existsByName(name)) {
            return "redirect:/director/users?classManageError";
        }
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(name);
        schoolClass.setYear(year);
        schoolClassRepository.save(schoolClass);
        return "redirect:/director/users?success";
    }

    @PostMapping("/director/classes/{id}/edit")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String editClass(@PathVariable Long id, @RequestParam String name, @RequestParam Integer year) {
        SchoolClass schoolClass = schoolClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        schoolClass.setName(name);
        schoolClass.setYear(year);
        try {
            schoolClassRepository.save(schoolClass);
            return "redirect:/director/users?success";
        } catch (RuntimeException e) {
            return "redirect:/director/users?classManageError";
        }
    }

    @PostMapping("/director/classes/{id}/delete")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String deleteClass(@PathVariable Long id) {
        if (userRepository.countByStudentClassId(id) > 0) {
            return "redirect:/director/users?classNotEmpty";
        }
        try {
            homeworkSubmissionRepository.findByHomeworkSchoolClassId(id).forEach(submission -> {
                if (submission.getFilePath() != null) {
                    fileService.deleteFile(submission.getFilePath());
                }
                homeworkSubmissionRepository.delete(submission);
            });
            homeworkRepository.findBySchoolClassId(id).forEach(homework -> homeworkService.deleteHomework(homework.getId()));
            attendanceRepository.deleteByScheduleSchoolClassId(id);
            scheduleRepository.deleteBySchoolClassId(id);
            testResultRepository.deleteByTestSchoolClassId(id);
            testRepository.deleteBySchoolClassId(id);
            videoLessonRepository.deleteBySchoolClassId(id);
            announcementRepository.deleteBySchoolClassId(id);
            schoolClassRepository.deleteById(id);
            return "redirect:/director/users?success";
        } catch (RuntimeException e) {
            return "redirect:/director/users?classManageError";
        }
    }

    @PostMapping("/director/users/{id}/class")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String updateStudentClass(@PathVariable Long id, @RequestParam Long classId) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Класс можно назначить только студенту");
        }

        SchoolClass newClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Класс не найден"));
        if (student.getStudentClass() != null && newClass.getYear() < student.getStudentClass().getYear()) {
            return "redirect:/director/users?classError";
        }

        student.setStudentClass(newClass);
        userRepository.save(student);
        return "redirect:/director/users?success";
    }

    @PostMapping("/director/users/{id}/subject")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String updateTeacherSubject(@PathVariable Long id, @RequestParam Long subjectId) {
        User teacher = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("Предмет можно назначить только учителю");
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));
        teacher.setTeacherSubject(subject);
        userRepository.save(teacher);
        return "redirect:/director/users?success";
    }

    @GetMapping("/director/statistics")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String directorStatistics(Model model) {
        List<Map<String, Object>> classStats = new ArrayList<>();
        for (SchoolClass schoolClass : schoolClassRepository.findAll()) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("className", schoolClass.getName());
            stat.put("average", gradeRepository.findAverageGradeByClassId(schoolClass.getId()));
            classStats.add(stat);
        }

        model.addAttribute("title", "Статистика");
        model.addAttribute("schoolAverage", gradeRepository.findSchoolAverageGrade());
        model.addAttribute("classStats", classStats);
        return "director-statistics";
    }

    private void addQuarterAccessModel(Model model) {
        List<Map<String, Object>> quarterAccesses = new ArrayList<>();
        for (int quarter = 1; quarter <= 4; quarter++) {
            Map<String, Object> item = new HashMap<>();
            item.put("quarter", quarter);
            item.put("open", gradeService.isQuarterOpen(quarter));
            quarterAccesses.add(item);
        }
        model.addAttribute("quarterAccesses", quarterAccesses);
    }

    private List<AnnouncementDto> getVisibleAnnouncements(User user) {
        if (user.getRole() == Role.STUDENT) {
            if (user.getStudentClass() == null) {
                return List.of();
            }
            return announcementService.getVisibleAnnouncementsForStudent(user.getStudentClass().getId(), null);
        }
        return announcementService.getAllAnnouncements();
    }
}
