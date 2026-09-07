package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.department.DepartmentResponse;
import com.abcbank.images.domain.entities.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentResponse toResponse(
            Department department
    );
}