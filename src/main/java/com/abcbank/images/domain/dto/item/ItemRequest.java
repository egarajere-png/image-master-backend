package com.abcbank.images.domain.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {

    @Size(max = 500)
    private String description;

    private String imageUrl;

    @NotBlank
    @Size(max = 50)
    private String idNumber;

    @NotBlank
    @Size(max = 150)
    private String customerName;

    @NotBlank
    @Size(max = 30)
    private String phoneNumber;
}
