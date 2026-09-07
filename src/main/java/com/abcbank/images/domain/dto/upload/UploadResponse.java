package com.abcbank.images.domain.dto.upload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {

    private Long itemId;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String imageUrl;

    private String description;

    private String idNumber;

    private String customerName;

    private String phoneNumber;

    private String status;

    private String currentQueue;
}