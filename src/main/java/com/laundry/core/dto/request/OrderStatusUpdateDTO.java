package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateDTO {
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    private Long modificadoPor;
}
