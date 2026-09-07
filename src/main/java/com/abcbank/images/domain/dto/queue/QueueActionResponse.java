package com.abcbank.images.domain.dto.queue;

import lombok.*;

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
