package com.example.taskmanager.service;

import com.example.taskmanager.dto.UploadVideoRequest;
import com.example.taskmanager.dto.VideoDto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface VideoLessonService {
    VideoDto uploadVideo(UploadVideoRequest request, MultipartFile file) throws IOException;
    List<VideoDto> getVideosByClass(Long classId);
    String getFilePath(Long id);
    void deleteVideo(Long id);
}
