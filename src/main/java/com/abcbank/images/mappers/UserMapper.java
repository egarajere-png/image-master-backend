package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.user.CurrentUserResponse;
import com.abcbank.images.domain.dto.user.UserResponse;
import com.abcbank.images.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // ============================================================
    // USER RESPONSE
    // ============================================================

    @Mapping(
            target = "departmentId",
            source = "department.id"
    )
    @Mapping(
            target = "departmentName",
            source = "department.name"
    )
    UserResponse toResponse(
            User user
    );


    // ============================================================
    // CURRENT USER RESPONSE
    // ============================================================

    @Mapping(
            target = "departmentId",
            source = "department.id"
    )
    @Mapping(
            target = "departmentName",
            source = "department.name"
    )
    CurrentUserResponse toCurrentUserResponse(
            User user
    );
}