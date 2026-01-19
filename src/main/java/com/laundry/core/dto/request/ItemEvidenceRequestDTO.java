package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEvidenceRequestDTO {
    @NotNull(message = "El order item ID es obligatorio")
    private Long orderItemId;

    @NotBlank(message = "El tipo de evidencia es obligatorio")
    private String tipo;

    private String etiqueta;
    private String fotoBase64;
    private Double geoLat;
    private Double geoLng;
    private Long creadoPor;
}
