package com.abcbank.images.domain.dto.upload;

import lombok.*;

/**
 * What POST /uploads returns after an image is stored and its
 * workflow item created. currentQueue reflects wherever the item
 * landed *after* the automatic UPLOAD action runs (ItemService.create),
 * not the initial queue it started in — so the frontend's "Queue
 * started successfully" screen shows where the item actually is.
 * idNumber/customerName/phoneNumber echoed back for the same reason.
 */


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