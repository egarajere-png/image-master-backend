package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.queue.QueueActionResponse;
import com.abcbank.images.domain.entities.QueueAction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QueueActionMapper {

    @Mapping(
            target = "queueId",
            source = "queue.id"
    )
    @Mapping(
            target = "queueName",
            source = "queue.name"
    )
    @Mapping(
            target = "actionId",
            source = "action.id"
    )
    @Mapping(
            target = "actionName",
            source = "action.name"
    )
    @Mapping(
            target = "actionDescription",
            source = "action.description"
    )
    QueueActionResponse toResponse(QueueAction queueAction);
}
