package com.abcbank.images.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Audit-log row written every time an item moves — one row per
 * action execution, capturing who did it, from which queue, to
 * which queue (or terminal outcome), and any comment left. This is
 * the data behind the workflow history/timeline shown on the item
 * detail page, and behind QueuePositionTrack's "how far has this
 * item progressed" view.
 */

@Entity
@Table(
        name = "item_transitions",
        indexes = {
                @Index(
                        name = "idx_item_transitions_item_id",
                        columnList = "item_id"
                ),
                @Index(
                        name = "idx_item_transitions_transition_id",
                        columnList = "transition_id"
                ),
                @Index(
                        name = "idx_item_transitions_performed_by",
                        columnList = "performed_by"
                ),
                @Index(
                        name = "idx_item_transitions_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transition_id", nullable = false)
    private Transition transition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_queue_id")
    private Queue sourceQueue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_queue_id")
    private Queue destinationQueue;

    @Column(name = "action_name", length = 100)
    private String actionName;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}