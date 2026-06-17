package com.appshopbanhang.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ImageResourceConfig implements WebMvcConfigurer {
    private final String imageStorageDir;

    public ImageResourceConfig(@Value("${app.image-storage-dir:uploads/images}") String imageStorageDir) {
        this.imageStorageDir = imageStorageDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path imageRoot = Paths.get(imageStorageDir).toAbsolutePath().normalize();
        String location = imageRoot.toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/images/**").addResourceLocations(location);
    }
}
