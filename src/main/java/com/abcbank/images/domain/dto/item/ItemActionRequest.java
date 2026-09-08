package com.abcbank.images.domain.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** Payload for POST /items/{id}/actions — just the action name and an optional comment. 
 * Everything else (authorization, routing) is resolved server-side by WorkflowService. */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemActionRequest {

    @NotBlank
    private String action;

    @Size(max = 2000)
    private String comment;
}
