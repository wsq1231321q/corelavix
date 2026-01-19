package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {
    @NotNull(message = "El order ID es obligatorio")
    private Long orderId;

    @NotNull(message = "El service ID es obligatorio")
    private Long serviceId;

    private String color;
    private String material;
    private Boolean esDelicado = false;
    private BigDecimal pesoKg;
    private Long bagId;
}
