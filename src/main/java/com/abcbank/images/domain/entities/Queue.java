package com.abcbank.images.domain.entities;

import com.abcbank.images.domain.enums.QueueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * A single stage in the workflow (e.g. "Teller", "Branch Manager").
 *
 * Queues are department-scoped: the `department` field (backed by
 * migration V14) means each branch has its own physical Teller
 * queue, its own Branch Manager queue, etc. — two branches never
 * share a queue row, even if they're named the same thing. Before
 * V14 this field existed on the entity but had no backing column,
 * which silently broke every non-admin user's "My Queue" page.
 *
 * `initial` marks the one queue per department where new items
 * enter (enforced by a partial unique index scoped to department_id,
 * not globally — each branch needs its own entry point).
 */

@Entity
@Table(
        name = "queues",
        indexes = {
                @Index(name = "idx_queues_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QueueStatus status;

    @Column(name = "is_initial", nullable = false)
    private boolean initial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        OffsetDateTime now = OffsetDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = QueueStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}