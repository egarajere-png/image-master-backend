package com.abcbank.images.security;

import com.abcbank.images.domain.entities.User;
import com.abcbank.images.exceptions.ResourceNotFoundException;
import com.abcbank.images.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Central admin-only gate, called at the top of every admin-write
 * endpoint. Passes immediately if the JWT carries ROLE_ADMIN;
 * otherwise falls back to checking whether the locally-mirrored
 * user's department is literally "Administration"
 * (UserService.ADMINISTRATION_DEPARTMENT_NAME) — kept as a fallback
 * in case admin status was granted by department assignment rather
 * than a Keycloak role.
 */

@Component
@RequiredArgsConstructor
public class AdminAccessGuard {

    private final UserService userService;

    // ============================================================
    // REQUIRE ADMINISTRATOR ACCESS
    // ============================================================

    /**
     * Administrator access is determined by membership in the
     * Administration department.
     *
     * The database relationship between User and Department is lazy,
     * so this method is transactional to ensure the Department can
     * safely be resolved while checking administrator access.
     */
    @Transactional(readOnly = true)
    public void requireAdmin(
            JwtAuthenticationToken authentication
    ) {

        if (authentication == null
                || authentication.getToken() == null) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        boolean adminRole = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        if (adminRole) {
            return;
        }

        String keycloakId =
                authentication.getToken().getSubject();

        User user;

        try {

            user = userService.getByKeycloakId(
                    keycloakId
            );

        } catch (ResourceNotFoundException exception) {

            throw new AccessDeniedException(
                    "Authenticated user does not exist locally"
            );
        }

        if (user.getDepartment() == null) {

            throw new AccessDeniedException(
                    "Administrator access is required"
            );
        }

        String departmentName =
                user.getDepartment().getName();

        if (departmentName == null
                || !departmentName.trim().equalsIgnoreCase(
                        UserService.ADMINISTRATION_DEPARTMENT_NAME
                )) {

            throw new AccessDeniedException(
                    "Administrator access is required"
            );
        }
    }
}