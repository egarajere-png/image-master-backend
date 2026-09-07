package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.item.ItemResponse;
import com.abcbank.images.domain.entities.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(
            target = "currentQueueId",
            source = "currentQueue.id"
    )
    @Mapping(
            target = "currentQueueName",
            source = "currentQueue.name"
    )
    @Mapping(
            target = "createdById",
            source = "createdBy.id"
    )
    @Mapping(
            target = "createdByName",
            source = "createdBy.name"
    )
    @Mapping(
            target = "departmentId",
            source = "department.id"
    )
    @Mapping(
            target = "departmentName",
            source = "department.name"
    )
    ItemResponse toResponse(Item item);
}
