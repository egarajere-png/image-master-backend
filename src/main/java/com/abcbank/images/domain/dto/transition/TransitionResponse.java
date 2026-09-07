package com.abcbank.images.domain.dto.transition;

import com.abcbank.images.domain.enums.WorkflowOutcome;
import lombok.*;

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