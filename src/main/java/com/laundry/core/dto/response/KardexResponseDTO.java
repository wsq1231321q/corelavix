package com.laundry.core.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KardexResponseDTO {
    private Long id;
    private Long orderId;
    private String tipo;
    private BigDecimal costo;
    private LocalDateTime createdAt;
}
