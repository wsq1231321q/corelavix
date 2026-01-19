package com.laundry.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientAddressRequestDTO {
    @NotNull(message = "El client ID es obligatorio")
    private Long clientId;

    private String etiqueta;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private Double latitud;
    private Double longitud;
}
