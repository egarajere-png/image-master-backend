package com.abcbank.images.domain.dto.user;

import lombok.*;

import java.time.OffsetDateTime;

/** What the frontend gets back for a user in admin lists (Admin → Users) 
 * — includes departmentId/departmentName so the assign/remove UI can show current placement. */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String name;

    private String keycloakId;

    private Long departmentId;

    private String departmentName;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
