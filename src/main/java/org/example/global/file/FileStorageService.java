package org.example.global.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String store(MultipartFile file);

    void delete(String key);

    String getFileUrl(String key);
}
