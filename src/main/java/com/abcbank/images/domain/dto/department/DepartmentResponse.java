package com.abcbank.images.domain.dto.department;

import lombok.*;

import java.time.OffsetDateTime;

/** What the frontend gets back for a department. */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private Long id;

    private String name;

    private String description;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}