package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.queue.QueueResponse;
import com.abcbank.images.domain.entities.Queue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QueueMapper {

    @Mapping(
            target = "departmentId",
            source = "department.id"
    )
    @Mapping(
            target = "departmentName",
            source = "department.name"
    )
    QueueResponse toResponse(Queue queue);
}
