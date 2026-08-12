package com.github.gr.aurora_bookstore.service;

import org.springframework.core.io.ClassPathResource;
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

        File targetFolder = new ClassPathResource("static/covers").getFile();
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        Path targetPath = Paths.get(targetFolder.getAbsolutePath(), cleanName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        File staticFolder = targetFolder.getParentFile();
        File classesFolder = staticFolder != null ? staticFolder.getParentFile() : null;
        File targetFolderParent = classesFolder != null ? classesFolder.getParentFile() : null;
        File backendFolder = targetFolderParent != null ? targetFolderParent.getParentFile() : null;

        if (backendFolder != null && backendFolder.exists()) {
            Path sourceFolder = Paths.get(backendFolder.getAbsolutePath(), "src", "main", "resources", "static", "covers");
            File sourceFolderFile = sourceFolder.toFile();
            if (sourceFolderFile.exists()) {
                Path sourcePath = Paths.get(sourceFolderFile.getAbsolutePath(), cleanName);
                Files.copy(file.getInputStream(), sourcePath, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        return "http://localhost:8080/covers/" + cleanName;
    }
}
