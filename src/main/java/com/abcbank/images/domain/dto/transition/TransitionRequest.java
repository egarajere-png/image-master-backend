package com.abcbank.images.domain.dto.transition;

import com.abcbank.images.domain.enums.WorkflowOutcome;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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