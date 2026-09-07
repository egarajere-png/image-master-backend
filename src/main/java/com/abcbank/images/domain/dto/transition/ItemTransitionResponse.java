package com.abcbank.images.domain.dto.transition;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTransitionResponse {

    private Long id;

    private Long itemId;

    private Long transitionId;

    private String actionName;

    private Long performedById;

    private String performedByName;

    private Long sourceQueueId;

    private String sourceQueueName;

    private Long destinationQueueId;

    private String destinationQueueName;

    private String comment;

    private OffsetDateTime createdAt;
}
