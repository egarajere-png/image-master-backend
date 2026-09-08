package com.abcbank.images.domain.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Payload for creating a workflow item (used both by the JSON
 * POST /items endpoint and internally by UploadService, which
 * builds one of these after storing the image file).
 *
 * idNumber, customerName, and phoneNumber are @NotBlank — added so
 * every item is traceable to a real customer, not just an image.
 * imageUrl/description stay optional since UploadService fills
 * imageUrl in after the physical file is stored.
 */


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {

    @Size(max = 500)
    private String description;

    private String imageUrl;

    @NotBlank
    @Size(max = 50)
    private String idNumber;

    @NotBlank
    @Size(max = 150)
    private String customerName;

    @NotBlank
    @Size(max = 30)
    private String phoneNumber;
}
