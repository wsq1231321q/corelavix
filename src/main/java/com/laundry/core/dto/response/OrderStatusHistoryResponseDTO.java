package com.laundry.core.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistoryResponseDTO {
    private Long id;
    private Long orderId;
    private String estadoAnterior;
    private String estadoNuevo;
    private Long modificadoPor;
    private String modificadoPorNombre;
    private LocalDateTime modificadoEn;
}
