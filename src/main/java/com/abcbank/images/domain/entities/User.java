package com.abcbank.images.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * A locally-mirrored Keycloak user, created on first login
 * (UserService.getOrCreateUser).
 *
 * isAdmin: mapped to a users.is_admin column that was missing from
 * every migration until V15 — the field existed in code from the
 * start but Hibernate schema validation only caught the gap once a
 * fully clean database was validated against it. The very first
 * user ever created is auto-promoted to admin.
 *
 * department: nullable — a brand-new user has no department until
 * an admin assigns one (Admin → Users / Departments), and can't
 * start or act on any workflow item until they do.
 */

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_keycloak_id", columnList = "keycloak_id"),
                @Index(name = "idx_users_department_id", columnList = "department_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "keycloak_id", nullable = false, unique = true, length = 255)
    private String keycloakId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}