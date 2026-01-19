package com.laundry.core.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private Long id;
    private Long orderId;
    private BigDecimal monto;
    private String metodo;
    private String estado;
    private LocalDateTime pagadoEn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
