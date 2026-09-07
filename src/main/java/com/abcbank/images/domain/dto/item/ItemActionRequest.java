package com.abcbank.images.domain.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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
