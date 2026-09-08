package com.abcbank.images.domain.dto.transition;

import lombok.*;

import java.time.OffsetDateTime;

/** One row of an item's workflow history — who acted, 
 * from which queue to which, when, and any comment. This is what powers the item detail page's timeline. */


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
