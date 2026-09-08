package com.abcbank.images.domain.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** Payload for creating/updating a department (branch) — just a name and optional description; 
 * nothing else about a department is mutable directly. */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequest {

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 255)
    private String description;
}