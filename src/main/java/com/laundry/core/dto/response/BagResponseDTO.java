package com.laundry.core.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BagResponseDTO {
    private Long id;
    private Long tenantId;
    private String qrCode;
    private Boolean disponible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
