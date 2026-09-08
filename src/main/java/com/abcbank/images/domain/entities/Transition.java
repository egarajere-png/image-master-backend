package com.abcbank.images.domain.entities;

import com.abcbank.images.domain.enums.WorkflowOutcome;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * The actual routing rule: for a given (QueueAction, Department)
 * pair, either move the item to `destinationQueue` or end the
 * workflow with `outcome` (COMPLETED/REMOVED) — never both, never
 * neither (enforced in TransitionService.validateTransitionConfiguration).
 *
 * This is what WorkflowService.executeAction looks up on every
 * action call, and what ItemService.create() relies on for the
 * automatic UPLOAD transition run right after an item is created.
 * The unique constraint (queue_action_id, department_id) means each
 * department configures its own routing even for actions shared
 * across all branches.
 */

@Entity
@Table(
        name = "transitions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_transitions_queue_action_department",
                        columnNames = {
                                "queue_action_id",
                                "department_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_transitions_queue_action_id",
                        columnList = "queue_action_id"
                ),
                @Index(
                        name = "idx_transitions_destination_queue_id",
                        columnList = "destination_queue_id"
                ),
                @Index(
                        name = "idx_transitions_department_id",
                        columnList = "department_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "queue_action_id",
            nullable = false
    )
    private QueueAction queueAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "destination_queue_id"
    )
    private Queue destinationQueue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "department_id",
            nullable = false
    )
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private WorkflowOutcome outcome;

    @Column(length = 255)
    private String description;

    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        createdAt = OffsetDateTime.now();
    }
}