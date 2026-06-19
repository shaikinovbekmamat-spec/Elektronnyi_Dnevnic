package com.example.taskmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file, String subDirectory) throws IOException {
        Path rootPath = Paths.get(uploadDir, subDirectory);
        if (!Files.exists(rootPath)) {
            Files.createDirectories(rootPath);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = rootPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return subDirectory + "/" + fileName;
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!path.isAbsolute()) {
                path = Paths.get(uploadDir).resolve(filePath);
            }
            Files.deleteIfExists(path.normalize());
        } catch (IOException e) {
            // Log error
        }
    }
}
