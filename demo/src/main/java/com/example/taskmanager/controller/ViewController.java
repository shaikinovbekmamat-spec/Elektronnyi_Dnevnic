package com.example.taskmanager.controller;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import com.example.taskmanager.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GradeService gradeService;
    private final HomeworkService homeworkService;
    private final AnnouncementService announcementService;
    private final ScheduleService scheduleService;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;

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
                model.addAttribute("schedule", scheduleService.getScheduleByClass(1L)); // Demo class ID
            } else {
                model.addAttribute("schedule", scheduleService.getScheduleByTeacher(user.getId()));
                model.addAttribute("classes", schoolClassRepository.findAll());
                model.addAttribute("subjects", subjectRepository.findAll());
            }
        }
        return "schedule";
    }

    @PostMapping("/schedule/add")
    public String addSchedule(@RequestParam Long classId,
                               @RequestParam Long subjectId,
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
        request.setTeacherId(userRepository.findByUsername(principal.getName()).get().getId());
        
        scheduleService.createSchedule(request);
        return "redirect:/schedule?success";
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
            model.addAttribute("announcements", announcementService.getAllAnnouncements());
        }
        
        return "dashboard";
    }

    @GetMapping("/grades")
    public String grades(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Журнал оценок");
        
        if (user != null) {
            if (user.getRole() == Role.STUDENT) {
                model.addAttribute("grades", gradeService.getGradesByStudent(user.getId()));
            } else if (user.getRole() == Role.TEACHER) {
                // In a real app, teacher would select a class/student
                model.addAttribute("students", userRepository.findAll()); // Simple list for demo
                model.addAttribute("subjects", subjectRepository.findAll());
            }
        }
        return "grades";
    }

    @PostMapping("/grades/add")
    public String addGrade(@RequestParam Long studentId, 
                           @RequestParam Long subjectId, 
                           @RequestParam Integer value,
                           @RequestParam String comment,
                           Principal principal) {
        CreateGradeRequest request = new CreateGradeRequest();
        request.setStudentId(studentId);
        request.setSubjectId(subjectId);
        request.setValue(value);
        request.setComment(comment);
        
        gradeService.createGrade(request, principal.getName());
        return "redirect:/grades?success";
    }

    @GetMapping("/homework")
    public String homework(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Домашние задания");
        
        if (user != null) {
            if (user.getRole() == Role.STUDENT) {
                // Assuming student is in some class, ideally we'd find their class
                model.addAttribute("homeworks", homeworkService.getHomeworkByClass(1L)); 
            } else {
                model.addAttribute("homeworks", homeworkService.getHomeworkByTeacher(user.getId()));
                model.addAttribute("classes", schoolClassRepository.findAll());
                model.addAttribute("subjects", subjectRepository.findAll());
            }
        }
        return "homework";
    }

    @GetMapping("/announcements")
    public String announcements(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("title", "Объявления");
        model.addAttribute("announcements", announcementService.getAllAnnouncements());
        model.addAttribute("classes", schoolClassRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "announcements";
    }

    @PostMapping("/announcements/add")
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
        
        homeworkService.createHomework(request, file);
        return "redirect:/homework?success";
    }
}
