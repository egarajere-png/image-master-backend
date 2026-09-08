package com.abcbank.images.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Join entity: which users are members of which queue. Membership
 * here is what WorkflowService checks before letting someone
 * execute an action — being in the right department alone isn't
 * enough, you also have to be assigned to that specific queue.
 */

@Entity
@Table(
        name = "queue_users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_queue_users_queue_user",
                        columnNames = {"queue_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_queue_users_queue_id", columnList = "queue_id"),
                @Index(name = "idx_queue_users_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}