package com.abcbank.images.domain.dto.queue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Payload for creating/updating a queue. departmentId is @NotNull —
 * queues used to be creatable without a department, which is
 * exactly what left every pre-V14 queue unreachable by non-admins.
 * Every queue now has to declare which branch it belongs to.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;

    private boolean initial;

    /**
     * The branch/department this queue belongs to. Queues are never
     * shared across departments — each department runs its own
     * complete set of workflow queues.
     */
    @NotNull
    private Long departmentId;
}