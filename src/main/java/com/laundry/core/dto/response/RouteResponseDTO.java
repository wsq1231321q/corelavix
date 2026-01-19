package com.laundry.core.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponseDTO {
    private Long id;
    private Long tenantId;
    private Long driverId;
    private String driverNombre;
    private String estado;
    private List<RouteOrderResponseDTO> routeOrders;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
