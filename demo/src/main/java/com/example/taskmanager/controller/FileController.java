package com.example.taskmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @GetMapping("/files/**")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) throws Exception {
        String requestUri = request.getRequestURI();
        String rawPath = requestUri.substring("/files/".length());
        String filePath = UriUtils.decode(rawPath, StandardCharsets.UTF_8)
                .replace('\\', '/');

        String uploadPrefix = uploadDir.replace('\\', '/');
        if (filePath.equals(uploadPrefix) || filePath.startsWith(uploadPrefix + "/")) {
            filePath = filePath.substring(uploadPrefix.length()).replaceFirst("^/", "");
        }

        Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path targetPath = uploadRoot.resolve(filePath).normalize();
        if (!targetPath.startsWith(uploadRoot) || !Files.exists(targetPath) || !Files.isRegularFile(targetPath)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(targetPath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        Resource resource = new FileSystemResource(targetPath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + targetPath.getFileName() + "\"")
                .body(resource);
    }
}
