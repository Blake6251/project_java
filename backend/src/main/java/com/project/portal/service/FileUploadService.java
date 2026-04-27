package com.project.kiosk.service;

import com.project.kiosk.exception.CustomException;
import com.project.kiosk.exception.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png");

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    public String saveImage(MultipartFile file) {
        validate(file);
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        Path dir = Paths.get(uploadPath);
        Path target = dir.resolve(filename);
        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "파일 저장에 실패했습니다.");
        }
        return target.toString();
    }

    public byte[] load(String imagePath) {
        try {
            return Files.readAllBytes(Paths.get(imagePath));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "이미지 파일을 읽을 수 없습니다.");
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "파일이 비어 있습니다.");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "파일 크기는 5MB 이하여야 합니다.");
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "jpg, jpeg, png 파일만 업로드 가능합니다.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
