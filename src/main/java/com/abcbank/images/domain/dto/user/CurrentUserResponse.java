package com.abcbank.images.domain.dto.user;

import lombok.*;

/**
 * What /auth/me returns for the logged-in user — drives AuthContext
 * on the frontend (myQueues, canStartQueue, isAdmin, department,
 * etc. all derive from this). isAdmin is independent of department:
 * a user can be flagged admin via Keycloak role sync in
 * UserService.getCurrentUser() even before their department is set
 * to Administration.
 */


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



