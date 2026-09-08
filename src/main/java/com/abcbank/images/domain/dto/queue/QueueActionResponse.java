package com.abcbank.images.domain.dto.queue;

import lombok.*;

/** Flattened view of a QueueAction — which action is wired to which queue, 
 * with both IDs and human-readable names so the frontend doesn't need a second lookup. */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueActionResponse {

    private Long id;

    private Long queueId;

    private String queueName;

    private Long actionId;

    private String actionName;

    private String actionDescription;
}
