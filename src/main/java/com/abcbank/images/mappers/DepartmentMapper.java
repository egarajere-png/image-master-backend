package com.abcbank.images.mappers;

import com.abcbank.images.domain.dto.department.DepartmentResponse;
import com.abcbank.images.domain.entities.Department;
import org.mapstruct.Mapper;

/** Straightforward entity → response mapping for Department.java 
 * — no custom logic beyond flattening a direct relationship or two into flat IDs/names. */

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentResponse toResponse(
            Department department
    );
}