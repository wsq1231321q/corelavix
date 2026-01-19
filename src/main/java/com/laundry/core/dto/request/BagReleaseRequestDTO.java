package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BagReleaseRequestDTO {
    @NotNull(message = "El bag ID es obligatorio")
    private Long bagId;
}
