package com.abcbank.images.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * A bank branch (e.g. "Westlands", "Koinange"), or the special
 * "Administration" department used to mark admin users (see
 * UserService.ADMINISTRATION_DEPARTMENT_NAME).
 *
 * Every User and every Queue belongs to exactly one Department —
 * this is the root of the branch-isolation model: a user only ever
 * sees queues/items scoped to their own department.
 */

@Entity
@Table(
        name = "departments",
        indexes = {
                @Index(name = "idx_departments_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(length = 255)
    private String description;

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