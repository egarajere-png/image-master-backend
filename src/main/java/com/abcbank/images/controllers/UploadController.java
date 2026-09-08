package com.abcbank.images.controllers;

import com.abcbank.images.domain.dto.upload.UploadResponse;
import com.abcbank.images.services.FileStorageService;
import com.abcbank.images.services.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * POST /uploads — the "Start Queue" endpoint. idNumber, customerName,
 * and phoneNumber are now required @RequestParams (previously only
 * the file was required), matching the same fields required on
 * ItemRequest. Also now computes adminRole and passes it through to
 * UploadService, same as ItemController.
 *
 * GET /uploads/files/{filename} serves a stored image back —
 * public, no auth required (see SecurityConfig), since it's just
 * static file content once you have the URL.
 */


@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;
    private final FileStorageService fileStorageService;

    /**
     * Upload an image and create a workflow item.
     */
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UploadResponse> uploadImage(
            @RequestParam("file")
            MultipartFile file,

            @RequestParam(value = "description", required = false)
            String description,

            @RequestParam("idNumber")
            String idNumber,

            @RequestParam("customerName")
            String customerName,

            @RequestParam("phoneNumber")
            String phoneNumber,

            Authentication authentication
    ) {

        UploadResponse response =
                uploadService.uploadImage(
                        file,
                        description,
                        idNumber,
                        customerName,
                        phoneNumber,
                        authentication.getName(),
                        authentication instanceof JwtAuthenticationToken jwt
                                && jwt.getAuthorities().stream()
                                .anyMatch(authority ->
                                        authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"))
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve an uploaded image.
     */
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String filename
    ) {

        Resource resource =
                fileStorageService.loadImage(filename);

        MediaType mediaType =
                determineMediaType(filename);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + URLEncoder.encode(
                                        filename,
                                        StandardCharsets.UTF_8
                                )
                                + "\""
                )
                .body(resource);
    }

    private MediaType determineMediaType(
            String filename
    ) {

        String lower =
                filename.toLowerCase();

        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }

        if (lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg")) {

            return MediaType.IMAGE_JPEG;
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}