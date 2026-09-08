package com.abcbank.images.domain.dto.item;

import com.abcbank.images.domain.enums.ItemStatus;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * What the frontend gets back for an item. Extended with
 * idNumber/customerName/phoneNumber (the required identity fields)
 * and departmentId/departmentName (via ItemMapper, mapped from the
 * item's own department — not the viewer's) so the UI can show
 * which branch an item belongs to.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {

    private Long id;

    private String description;

    private String imageUrl;

    private String idNumber;

    private String customerName;

    private String phoneNumber;

    private Long departmentId;

    private String departmentName;

    private Long currentQueueId;

    private String currentQueueName;

    private ItemStatus status;

    private Long createdById;

    private String createdByName;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
