package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.queue.QueueResponse;
import com.abcbank.images.domain.entities.Queue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Entity → response mapping for Queue. departmentId/departmentName mapped explicitly from queue.department 
 * — added so the frontend can show and validate each queue's branch. */

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
