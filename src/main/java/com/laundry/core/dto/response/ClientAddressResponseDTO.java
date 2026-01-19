package com.laundry.core.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientAddressResponseDTO {
    private Long id;
    private Long clientId;
    private String etiqueta;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
