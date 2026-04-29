package org.example.global.file;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Profile("!s3")
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리를 생성할 수 없습니다.", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String extension = getExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;

        try {
            Path targetLocation = this.uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다: " + originalFileName, e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Path filePath = resolveAndValidate(key);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제에 실패했습니다: " + key, e);
        }
    }

    @Override
    public String getFileUrl(String key) {
        return "/api/products/images/" + key;
    }

    public Path getFilePath(String fileName) {
        return resolveAndValidate(fileName);
    }

    private Path resolveAndValidate(String fileName) {
        Path filePath = this.uploadPath.resolve(fileName).normalize();
        if (!filePath.startsWith(this.uploadPath)) {
            throw new SecurityException("잘못된 파일 경로입니다.");
        }
        return filePath;
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
