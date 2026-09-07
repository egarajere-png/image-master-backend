package com.abcbank.images.domain.dto.item;

import com.abcbank.images.domain.enums.ItemStatus;
import lombok.*;

import java.time.OffsetDateTime;

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
