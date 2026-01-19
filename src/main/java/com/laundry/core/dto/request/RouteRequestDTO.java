package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteRequestDTO {
    @NotNull(message = "El tenant ID es obligatorio")
    private Long tenantId;

    @NotNull(message = "El driver ID es obligatorio")
    private Long driverId;
}
