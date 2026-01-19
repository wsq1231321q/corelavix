package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    @NotNull(message = "El tenant ID es obligatorio")
    private Long tenantId;

    @NotNull(message = "El client ID es obligatorio")
    private Long clientId;

    private LocalDate fechaPrometida;
}
