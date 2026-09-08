package com.abcbank.images.services;

import com.abcbank.images.domain.dto.user.CurrentUserResponse;
import com.abcbank.images.domain.dto.user.UserResponse;
import com.abcbank.images.domain.entities.User;
import com.abcbank.images.exceptions.ResourceNotFoundException;
import com.abcbank.images.mappers.UserMapper;
import com.abcbank.images.repositories.DepartmentRepository;
import com.abcbank.images.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User lookup/creation from Keycloak identity, plus the current-
 * user resolution used by /auth/me.
 *
 * getOrCreateUser() auto-promotes the very first user ever created
 * to admin and drops them into the "Administration" department —
 * this only works now that is_admin actually exists as a column
 * (migration V15; the field was mapped in code from the start but
 * had no backing column until then).
 *
 * getCurrentUser() also syncs admin status from the Keycloak JWT's
 * ROLE_ADMIN authority on every call (not just at creation), and
 * moves a newly-admin user into the Administration department if
 * they aren't in it yet — isAdmin itself stays the actual source of
 * truth for authorization (see AdminAccessGuard), department
 * placement is a convenience that follows it.
 */

@Service
@RequiredArgsConstructor
public class UserService {

private final UserRepository userRepository;
private final DepartmentRepository departmentRepository;
private final UserMapper userMapper;

public static final String ADMINISTRATION_DEPARTMENT_NAME =
        "Administration";

// ============================================================
// GET USER BY KEYCLOAK ID
// ============================================================

@Transactional(readOnly = true)
public User getByKeycloakId(
        String keycloakId
) {

    if (keycloakId == null
            || keycloakId.isBlank()) {

        throw new ResourceNotFoundException(
                "Keycloak user ID is required"
        );
    }

    return userRepository
            .findByKeycloakId(keycloakId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "User not found for Keycloak ID: "
                                    + keycloakId
                    )
            );
}

// ============================================================
// GET OR CREATE USER
// ============================================================

@Transactional
public User getOrCreateUser(
        String keycloakId,
        String username,
        String email,
        String firstName,
        String lastName
) {

    if (keycloakId == null
            || keycloakId.isBlank()) {

        throw new ResourceNotFoundException(
                "Keycloak user ID is required"
        );
    }

    return userRepository
            .findByKeycloakId(keycloakId)
            .orElseGet(() -> {

                String name =
                        buildDisplayName(
                                username,
                                email,
                                firstName,
                                lastName
                        );

                boolean isFirstUserEver =
                        userRepository.count() == 0;

                User.UserBuilder builder =
                        User.builder()
                                .keycloakId(keycloakId)
                                .name(name)
                                .isAdmin(isFirstUserEver);

                if (isFirstUserEver) {

                    departmentRepository
                            .findByNameIgnoreCase(
                                    ADMINISTRATION_DEPARTMENT_NAME
                            )
                            .ifPresent(
                                    builder::department
                            );
                }

                return userRepository.save(
                        builder.build()
                );
            });
}

// ============================================================
// GET USER BY DATABASE ID
// ============================================================

@Transactional(readOnly = true)
public User getById(
        Long id
) {

    return userRepository
            .findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "User not found: " + id
                    )
            );
}

// ============================================================
// GET USER RESPONSE
// ============================================================

@Transactional(readOnly = true)
public UserResponse getUserResponseById(
        Long id
) {

    return userMapper.toResponse(
            getById(id)
    );
}

// ============================================================
// GET ALL USERS
// ============================================================

@Transactional(readOnly = true)
public List<UserResponse> getAll() {

    return userRepository
            .findAll()
            .stream()
            .map(userMapper::toResponse)
            .toList();
}

// ============================================================
// GET CURRENT USER
// ============================================================

@Transactional
public CurrentUserResponse getCurrentUser(
        String keycloakId,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean adminRole
) {

    User user =
            getOrCreateUser(
                    keycloakId,
                    username,
                    email,
                    firstName,
                    lastName
            );

    /*
     * Synchronize administrator privileges from the
     * authenticated Keycloak user.
     */
    if (adminRole
            && !Boolean.TRUE.equals(
                    user.getIsAdmin()
            )) {

        user.setIsAdmin(true);
    }

    /*
     * Administrators belong to the Administration department.
     * The database User.isAdmin field remains the authorization
     * source of truth.
     */
    if (Boolean.TRUE.equals(
            user.getIsAdmin()
    ) && !isAdministrationUser(user)) {

        departmentRepository
                .findByNameIgnoreCase(
                        ADMINISTRATION_DEPARTMENT_NAME
                )
                .ifPresent(
                        user::setDepartment
                );
    }

    Long departmentId = null;
    String departmentName = null;

    if (user.getDepartment() != null) {

        departmentId =
                user.getDepartment().getId();

        departmentName =
                user.getDepartment().getName();
    }

    return CurrentUserResponse
            .builder()
            .id(user.getId())
            .name(user.getName())
            .keycloakId(user.getKeycloakId())
            .departmentId(departmentId)
            .departmentName(departmentName)
            .isAdmin(
                    Boolean.TRUE.equals(
                            user.getIsAdmin()
                    )
            )
            .build();
}

// ============================================================
// CHECK ADMINISTRATION DEPARTMENT
// ============================================================

private boolean isAdministrationUser(
        User user
) {

    return user.getDepartment() != null
            && user.getDepartment().getName() != null
            && user.getDepartment()
                    .getName()
                    .trim()
                    .equalsIgnoreCase(
                            ADMINISTRATION_DEPARTMENT_NAME
                    );
}

// ============================================================
// BUILD DISPLAY NAME
// ============================================================

private String buildDisplayName(
        String username,
        String email,
        String firstName,
        String lastName
) {

    String first =
            firstName != null
                    ? firstName.trim()
                    : "";

    String last =
            lastName != null
                    ? lastName.trim()
                    : "";

    String fullName =
            (first + " " + last).trim();

    if (!fullName.isBlank()) {
        return fullName;
    }

    if (username != null
            && !username.isBlank()) {

        return username;
    }

    if (email != null
            && !email.isBlank()) {

        return email;
    }

    return "Keycloak User";
}

}
