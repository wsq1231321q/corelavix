package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BagRequestDTO {
    @NotNull(message = "El tenant ID es obligatorio")
    private Long tenantId;

    @NotBlank(message = "El código QR es obligatorio")
    private String qrCode;

    private Boolean disponible = true;
}
