package com.abcbank.images.domain.dto.transition;

import com.abcbank.images.domain.enums.WorkflowOutcome;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/** Payload for configuring a Transition: 
 * which (queueAction, department) pair, and either a destinationQueueId or a terminal outcome. */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransitionRequest {

    @NotNull
    private Long queueActionId;

    @NotNull
    private Long departmentId;

    private Long destinationQueueId;

    private WorkflowOutcome outcome;

    @Size(max = 255)
    private String description;
}