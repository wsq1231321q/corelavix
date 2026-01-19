package com.laundry.core.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private Long tenantId;
    private Long clientId;
    private String clienteNombre;
    private String estado;
    private LocalDate fechaPrometida;
    private BigDecimal total;
    private BigDecimal saldoPendiente;
    private List<OrderItemResponseDTO> items;
    private List<PaymentResponseDTO> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
