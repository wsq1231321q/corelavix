package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteOrderRequestDTO {
    @NotNull(message = "El route ID es obligatorio")
    private Long routeId;

    @NotNull(message = "El order ID es obligatorio")
    private Long orderId;

    private Integer ordenDeParada;
}
