package com.example.taskmanager.service;

import com.example.taskmanager.dto.UploadVideoRequest;
import com.example.taskmanager.dto.VideoDto;
import com.example.taskmanager.model.VideoLesson;
import com.example.taskmanager.repository.SchoolClassRepository;
import com.example.taskmanager.repository.SubjectRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.repository.VideoLessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoLessonServiceImpl implements VideoLessonService {

    private final VideoLessonRepository videoLessonRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final FileService fileService;

    @Override
    public VideoDto uploadVideo(UploadVideoRequest request, MultipartFile file) throws IOException {
        var teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Учитель не найден"));
        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));
        var schoolClass = schoolClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Класс не найден"));

        VideoLesson video = new VideoLesson();
        video.setTitle(request.getTitle());
        video.setTeacher(teacher);
        video.setSubject(subject);
        video.setSchoolClass(schoolClass);
        video.setUploadedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            String filePath = fileService.saveFile(file, "videos");
            video.setFilePath(filePath);
        } else {
            throw new RuntimeException("Файл видео отсутствует");
        }

        return toDto(videoLessonRepository.save(video));
    }

    @Override
    public List<VideoDto> getVideosByClass(Long classId) {
        return videoLessonRepository.findBySchoolClassId(classId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public String getFilePath(Long id) {
        return videoLessonRepository.findById(id)
                .map(VideoLesson::getFilePath)
                .orElseThrow(() -> new RuntimeException("Видео не найдено"));
    }

    @Override
    public void deleteVideo(Long id) {
        videoLessonRepository.findById(id).ifPresent(v -> {
            fileService.deleteFile(v.getFilePath());
            videoLessonRepository.deleteById(id);
        });
    }

    private VideoDto toDto(VideoLesson video) {
        VideoDto dto = new VideoDto();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setSubjectName(video.getSubject().getName());
        dto.setClassName(video.getSchoolClass().getName());
        dto.setTeacherName(video.getTeacher().getFullName());
        dto.setFilePath(video.getFilePath());
        dto.setUploadedAt(video.getUploadedAt());
        return dto;
    }
}
