package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.transition.TransitionResponse;
import com.abcbank.images.domain.entities.Transition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Entity → response mapping for Transition. Flattens three levels of joins (queueAction.queue, queueAction.action, department, destinationQueue) 
 * into flat IDs/names so the frontend doesn't have to walk the object graph itself. */

@Mapper(componentModel = "spring")
public interface TransitionMapper {

    @Mapping(
            target = "sourceQueueId",
            source = "queueAction.queue.id"
    )
    @Mapping(
            target = "sourceQueueName",
            source = "queueAction.queue.name"
    )
    @Mapping(
            target = "actionId",
            source = "queueAction.action.id"
    )
    @Mapping(
            target = "actionName",
            source = "queueAction.action.name"
    )
    @Mapping(
            target = "queueActionId",
            source = "queueAction.id"
    )
    @Mapping(
            target = "departmentId",
            source = "department.id"
    )
    @Mapping(
            target = "departmentName",
            source = "department.name"
    )
    @Mapping(
            target = "destinationQueueId",
            source = "destinationQueue.id"
    )
    @Mapping(
            target = "destinationQueueName",
            source = "destinationQueue.name"
    )
    TransitionResponse toResponse(
            Transition transition
    );
}