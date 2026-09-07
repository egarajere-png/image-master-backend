package com.abcbank.images.domain.dto.queue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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