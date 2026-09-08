package com.abcbank.images.domain.dto.action;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** Payload for creating/updating an Action (a workflow verb like UPLOAD or AMEND). */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;
}
