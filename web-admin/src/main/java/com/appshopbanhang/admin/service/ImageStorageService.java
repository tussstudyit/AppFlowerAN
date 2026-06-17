package com.appshopbanhang.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "webp");

    private final Path rootDirectory;

    public ImageStorageService(@Value("${app.image-storage-dir:uploads/images}") String imageStorageDir) {
        this.rootDirectory = Paths.get(imageStorageDir).toAbsolutePath().normalize();
    }

    public String save(String folder, MultipartFile image) throws IOException {
        String cleanFolder = sanitizePathPart(folder);
        String extension = resolveExtension(image);
        String filename = UUID.randomUUID() + "." + extension;
        Path targetDirectory = rootDirectory.resolve(cleanFolder).normalize();
        Files.createDirectories(targetDirectory);
        Files.copy(image.getInputStream(), targetDirectory.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return "/images/" + cleanFolder + "/" + filename;
    }

    private String sanitizePathPart(String value) {
        String cleaned = value == null ? "" : value.replace("\\", "/").replaceAll("[^a-zA-Z0-9/_-]", "");
        if (!StringUtils.hasText(cleaned) || cleaned.contains("..") || cleaned.startsWith("/")) {
            throw new IllegalArgumentException("Thu muc anh khong hop le");
        }
        return cleaned;
    }

    private String resolveExtension(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (!StringUtils.hasText(extension)) {
            extension = extensionFromContentType(image.getContentType());
        }
        extension = extension == null ? "png" : extension.toLowerCase(Locale.ROOT);
        if ("jpeg".equals(extension)) {
            extension = "jpg";
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            extension = "png";
        }
        return extension;
    }

    private String extensionFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> "jpg";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> "png";
        };
    }
}
