package com.abcbank.images.domain.dto.action;

import lombok.*;

import java.time.OffsetDateTime;

/** What the frontend gets back for an Action. */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionResponse {

    private Long id;

    private String name;

    private String description;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
