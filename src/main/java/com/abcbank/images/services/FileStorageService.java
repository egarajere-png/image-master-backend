package com.abcbank.images.services;

import com.abcbank.images.exceptions.FileProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png"
    );

    private final Path imageStorageLocation;

    public FileStorageService(
            @Value("${file.storage.location:uploads/images}")
            String storageLocation
    ) {

        try {

            this.imageStorageLocation =
                    Paths.get(storageLocation)
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(this.imageStorageLocation);

        } catch (IOException ex) {

            throw new FileProcessingException(
                    "Could not initialize image storage location",
                    ex
            );
        }
    }

    /**
     * Stores an uploaded image and returns the generated filename.
     */
    public String storeImage(MultipartFile file) {

        validateFile(file);

        String originalFilename =
                StringUtils.cleanPath(
                        file.getOriginalFilename() == null
                                ? ""
                                : file.getOriginalFilename()
                );

        String extension =
                getExtension(originalFilename);

        String generatedFilename =
                UUID.randomUUID() + extension;

        Path targetLocation =
                imageStorageLocation.resolve(generatedFilename)
                        .normalize();

        // Prevent path traversal
        if (!targetLocation.startsWith(imageStorageLocation)) {
            throw new FileProcessingException(
                    "Invalid file path"
            );
        }

        try (InputStream inputStream = file.getInputStream()) {

            Files.copy(
                    inputStream,
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return generatedFilename;

        } catch (IOException ex) {

            throw new FileProcessingException(
                    "Could not store uploaded image",
                    ex
            );
        }
    }

    /**
     * Loads an image as a Spring Resource.
     */
    public Resource loadImage(String filename) {

        try {

            Path filePath =
                    imageStorageLocation.resolve(filename)
                            .normalize();

            if (!filePath.startsWith(imageStorageLocation)) {
                throw new FileProcessingException(
                        "Invalid file path"
                );
            }

            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );

            if (!resource.exists() ||
                    !resource.isReadable()) {

                throw new FileProcessingException(
                        "Image file not found: " + filename
                );
            }

            return resource;

        } catch (MalformedURLException ex) {

            throw new FileProcessingException(
                    "Could not load image: " + filename,
                    ex
            );
        }
    }

    /**
     * Deletes an image from storage.
     */
    public void deleteImage(String filename) {

        if (filename == null || filename.isBlank()) {
            return;
        }

        try {

            Path filePath =
                    imageStorageLocation.resolve(filename)
                            .normalize();

            if (!filePath.startsWith(imageStorageLocation)) {
                throw new FileProcessingException(
                        "Invalid file path"
                );
            }

            Files.deleteIfExists(filePath);

        } catch (IOException ex) {

            throw new FileProcessingException(
                    "Could not delete image: " + filename,
                    ex
            );
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new FileProcessingException(
                    "Image file is required"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(
                        contentType.toLowerCase()
                )) {

            throw new FileProcessingException(
                    "Only JPEG and PNG images are allowed"
            );
        }

        String originalFilename =
                file.getOriginalFilename();

        if (originalFilename == null ||
                originalFilename.isBlank()) {

            throw new FileProcessingException(
                    "Image filename is required"
            );
        }
    }

    private String getExtension(String filename) {

        String extension =
                StringUtils.getFilenameExtension(filename);

        if (extension == null ||
                extension.isBlank()) {

            throw new FileProcessingException(
                    "Image file must have an extension"
            );
        }

        return "." + extension.toLowerCase();
    }
}