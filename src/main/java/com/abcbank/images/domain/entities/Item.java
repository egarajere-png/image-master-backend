package com.abcbank.images.domain.entities;

import com.abcbank.images.domain.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * A single workflow item — one uploaded image plus the customer
 * details that must accompany it (idNumber, customerName,
 * phoneNumber, added so every image is traceable to a real
 * customer, not just a file).
 *
 * Two fields were added on top of the original entity to support
 * branch isolation and traceability:
 *  - department: the branch this item was created in (set once,
 *    from the creating Teller's own department, in ItemService.create()).
 *    This is what stops Koinange staff from ever seeing a Westlands item.
 *  - idNumber / customerName / phoneNumber: required at upload time,
 *    editable later via the AMEND action.
 *
 * currentQueue tracks where the item is right now; status flips to
 * COMPLETED/REMOVED once a transition resolves to a terminal outcome
 * instead of another queue.
 */

@Entity
@Table(
        name = "items",
        indexes = {
                @Index(
                        name = "idx_items_current_queue_id",
                        columnList = "current_queue_id"
                ),
                @Index(
                        name = "idx_items_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_items_created_by",
                        columnList = "created_by"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "id_number", length = 50)
    private String idNumber;

    @Column(name = "customer_name", length = 150)
    private String customerName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_queue_id")
    private Queue currentQueue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

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
            status = ItemStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}