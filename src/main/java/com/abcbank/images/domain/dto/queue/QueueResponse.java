package com.abcbank.images.domain.dto.queue;

import com.abcbank.images.domain.enums.QueueStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueResponse {

    private Long id;

    private String name;

    private String description;

    private QueueStatus status;

    private boolean initial;

    private Long departmentId;

    private String departmentName;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}