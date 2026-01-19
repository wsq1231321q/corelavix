package com.laundry.core.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteOrderResponseDTO {
    private Long id;
    private Long routeId;
    private Long orderId;
    private String ordenNumero;
    private String clienteNombre;
    private String direccion;
    private Integer ordenDeParada;
}
