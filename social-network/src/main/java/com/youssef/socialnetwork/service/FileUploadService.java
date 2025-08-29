package com.youssef.socialnetwork.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    private final String uploadDir = "uploads/";

    public String saveFile(MultipartFile file) {
        try {
            Path path = Paths.get(uploadDir + UUID.randomUUID() + "_" + file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return "/uploads/" + path.getFileName().toString(); // URL للملف
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}