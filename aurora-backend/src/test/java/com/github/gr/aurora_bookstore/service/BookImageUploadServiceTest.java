package com.github.gr.aurora_bookstore.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class BookImageUploadServiceTest {

    private BookImageUploadService service;
    private String uploadedFilename;

    @BeforeEach
    void setUp() {
        service = new BookImageUploadService();
        uploadedFilename = null;
    }

    @AfterEach
    void tearDown() throws IOException {
        if (uploadedFilename != null) {
            java.io.File targetFolder = new org.springframework.core.io.ClassPathResource("static/covers").getFile();
            Files.deleteIfExists(Paths.get(targetFolder.getAbsolutePath(), uploadedFilename));

            java.io.File staticFolder = targetFolder.getParentFile();
            java.io.File classesFolder = staticFolder != null ? staticFolder.getParentFile() : null;
            java.io.File targetFolderParent = classesFolder != null ? classesFolder.getParentFile() : null;
            java.io.File backendFolder = targetFolderParent != null ? targetFolderParent.getParentFile() : null;

            if (backendFolder != null) {
                java.nio.file.Path sourceFolder = Paths.get(backendFolder.getAbsolutePath(), "src", "main", "resources", "static", "covers");
                Files.deleteIfExists(Paths.get(sourceFolder.toFile().getAbsolutePath(), uploadedFilename));
            }
        }
    }

    @Test
    void uploadImage_WithEmptyFile_ShouldThrowIllegalArgumentException() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.uploadImage(emptyFile);
        });

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void uploadImage_WithInvalidContentType_ShouldThrowIllegalArgumentException() {
        MockMultipartFile txtFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello".getBytes());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.uploadImage(txtFile);
        });

        assertEquals("File is not an image", exception.getMessage());
    }

    @Test
    void uploadImage_WithValidImage_ShouldSaveFileAndReturnUrl() throws IOException {
        MockMultipartFile validFile = new MockMultipartFile("file", "test.png", "image/png", "fake-image-content".getBytes());

        String url = service.uploadImage(validFile);

        assertNotNull(url);
        assertTrue(url.startsWith("http://localhost:8080/covers/"));
        assertTrue(url.endsWith(".png"));

        uploadedFilename = url.substring(url.lastIndexOf("/") + 1);
        assertTrue(Files.exists(Paths.get("/home/gr/aurora-book-store/aurora-backend/target/classes/static/covers", uploadedFilename)));
    }
}
