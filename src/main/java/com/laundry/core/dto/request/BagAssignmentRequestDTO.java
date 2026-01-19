package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BagAssignmentRequestDTO {
    @NotNull(message = "El bag ID es obligatorio")
    private Long bagId;

    @NotNull(message = "El order ID es obligatorio")
    private Long orderId;
}
