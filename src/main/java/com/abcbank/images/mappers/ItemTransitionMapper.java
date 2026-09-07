package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.transition.ItemTransitionResponse;
import com.abcbank.images.domain.entities.ItemTransition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemTransitionMapper {

    @Mapping(
            target = "itemId",
            source = "item.id"
    )
    @Mapping(
            target = "transitionId",
            source = "transition.id"
    )
    @Mapping(
            target = "performedById",
            source = "performedBy.id"
    )
    @Mapping(
            target = "performedByName",
            source = "performedBy.name"
    )
    @Mapping(
            target = "sourceQueueId",
            source = "sourceQueue.id"
    )
    @Mapping(
            target = "sourceQueueName",
            source = "sourceQueue.name"
    )
    @Mapping(
            target = "destinationQueueId",
            source = "destinationQueue.id"
    )
    @Mapping(
            target = "destinationQueueName",
            source = "destinationQueue.name"
    )
    ItemTransitionResponse toResponse(ItemTransition itemTransition);
}
