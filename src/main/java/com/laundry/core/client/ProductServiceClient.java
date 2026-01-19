package com.laundry.core.client;

import com.laundry.core.config.TenantContext;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@Component
@Getter
@Setter
@NoArgsConstructor
public class ProductServiceClient {  // ← ELIMINADO @RequiredArgsConstructor

    private WebClient productWebClient;

    // Spring inyectará automáticamente este campo con @Qualifier
    public ProductServiceClient(@Qualifier("productWebClient") WebClient productWebClient) {
        this.productWebClient = productWebClient;
    }

    public ServiceDTO getService(Long serviceId) {
        return productWebClient.get()
                .uri("/servicios/{id}", serviceId)
                .header("X-Tenant-ID", String.valueOf(TenantContext.getTenantId()))
                .retrieve()
                .bodyToMono(ServiceDTO.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    public PrecioDTO getActivePrice(Long serviceId) {
        return productWebClient.get()
                .uri("/precios/activo/{id}", serviceId)
                .header("X-Tenant-ID", String.valueOf(TenantContext.getTenantId()))
                .retrieve()
                .bodyToMono(PrecioDTO.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    public ReglaDTO getServiceRules(Long serviceId) {
        return productWebClient.get()
                .uri("/reglas/por-servicio/{servicioId}", serviceId)
                .header("X-Tenant-ID", String.valueOf(TenantContext.getTenantId()))
                .retrieve()
                .bodyToMono(ReglaDTO.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceDTO {
        private Long id;
        private String nombre;
        private String descripcion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrecioDTO {
        private Long id;
        private Long servicioId;
        private BigDecimal precio;
        private String pricingType; // FIJO, POR_PESO
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReglaDTO {
        private Long id;
        private Long servicioId;
        private Boolean requiresWeight;
        private BigDecimal minWeight;
        private BigDecimal extraCharge;
    }
}
