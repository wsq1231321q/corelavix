package com.laundry.core.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDTO {
    private Long id;
    private Long tenantId;
    private String nombreCompleto;
    private String telefono;
    private String whatsapp;
    private String email;
    private Boolean esVip;
    private List<ClientAddressResponseDTO> addresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
