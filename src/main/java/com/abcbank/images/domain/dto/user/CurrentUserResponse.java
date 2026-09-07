package com.abcbank.images.domain.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentUserResponse {

    private Long id;

    private String name;

    private String keycloakId;

    private Long departmentId;

    private String departmentName;

    /** Whether this user has admin privileges (independent of operational department). */
    private Boolean isAdmin;
}



