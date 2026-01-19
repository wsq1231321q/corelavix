package com.laundry.core.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KardexRequestDTO {
    @NotNull(message = "El order ID es obligatorio")
    private Long orderId;

    @NotBlank(message = "El tipo de costo es obligatorio")
    private String tipo;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo debe ser mayor a 0")
    private BigDecimal costo;
}
