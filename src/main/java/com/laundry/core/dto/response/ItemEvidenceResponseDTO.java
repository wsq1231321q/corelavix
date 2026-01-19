package com.laundry.core.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEvidenceResponseDTO {
    private Long id;
    private Long orderItemId;
    private String tipo;
    private String etiqueta;
    private String fotoBase64;
    private Double geoLat;
    private Double geoLng;
    private Long creadoPor;
    private LocalDateTime createdAt;
}
