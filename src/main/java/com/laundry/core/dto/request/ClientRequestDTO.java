package com.laundry.core.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequestDTO {
    @NotNull(message = "El tenant ID es obligatorio")
    private Long tenantId;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    private String telefono;
    private String whatsapp;

    @Email(message = "El email debe ser válido")
    private String email;

    private Boolean esVip = false;
}
