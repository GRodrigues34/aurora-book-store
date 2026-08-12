package com.github.gr.aurora_bookstore.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class BookImageUploadService {

    private static final String SOURCE_DIR = "/home/gr/aurora-book-store/aurora-backend/src/main/resources/static/covers";
    private static final String TARGET_DIR = "/home/gr/aurora-book-store/aurora-backend/target/classes/static/covers";

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File is not an image");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String cleanName = UUID.randomUUID().toString() + extension;

        File targetFolder = new File(TARGET_DIR);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        Path targetPath = Paths.get(TARGET_DIR, cleanName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        File sourceFolder = new File(SOURCE_DIR);
        if (sourceFolder.exists()) {
            Path sourcePath = Paths.get(SOURCE_DIR, cleanName);
            Files.copy(file.getInputStream(), sourcePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return "http://localhost:8080/covers/" + cleanName;
    }
}
