package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.action.ActionResponse;
import com.abcbank.images.domain.entities.Action;
import org.mapstruct.Mapper;

/** Straightforward entity → response mapping for Action.java 
 * — no custom logic beyond flattening a direct relationship or two into flat IDs/names. */

@Mapper(componentModel = "spring")
public interface ActionMapper {

    ActionResponse toResponse(Action action);
}
