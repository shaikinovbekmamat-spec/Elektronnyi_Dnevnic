package com.example.taskmanager.controller;

import com.example.taskmanager.dto.UploadVideoRequest;
import com.example.taskmanager.dto.VideoDto;
import com.example.taskmanager.service.VideoLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoLessonService videoLessonService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<VideoDto> uploadVideo(
            @RequestPart("request") UploadVideoRequest request,
            @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(videoLessonService.uploadVideo(request, file));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<VideoDto>> getVideosByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(videoLessonService.getVideosByClass(classId));
    }

    @GetMapping(value = "/{id}/stream", produces = "video/mp4")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long id) {
        String filePath = videoLessonService.getFilePath(id);
        Path path = Paths.get(filePath);
        Resource resource = new FileSystemResource(path);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoLessonService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}
