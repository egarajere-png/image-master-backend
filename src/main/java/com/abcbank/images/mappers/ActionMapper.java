package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.action.ActionResponse;
import com.abcbank.images.domain.entities.Action;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActionMapper {

    ActionResponse toResponse(Action action);
}
