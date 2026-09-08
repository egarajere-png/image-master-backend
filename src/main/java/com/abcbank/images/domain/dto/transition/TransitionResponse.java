package com.abcbank.images.domain.dto.transition;

import com.abcbank.images.domain.enums.WorkflowOutcome;
import lombok.*;

/** Full readable view of a Transition — source queue, action, department, 
 * and destination/outcome all flattened out via TransitionMapper, 
 * since the raw entity is three joins deep. */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransitionResponse {

    private Long id;

    private Long queueActionId;

    private Long sourceQueueId;

    private String sourceQueueName;

    private Long actionId;

    private String actionName;

    private Long departmentId;

    private String departmentName;

    private Long destinationQueueId;

    private String destinationQueueName;

    private WorkflowOutcome outcome;

    private String description;
}