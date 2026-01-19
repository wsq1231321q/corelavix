package com.laundry.core.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {
    private Long id;
    private Long orderId;
    private Long serviceId;
    private String servicioNombre;
    private String color;
    private String material;
    private Boolean esDelicado;
    private BigDecimal pesoKg;
    private String estado;
    private Long bagId;
    private String bagQrCode;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private List<ItemEvidenceResponseDTO> evidences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
