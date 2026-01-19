package com.laundry.core.client;

import com.laundry.core.config.TenantContext;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserServiceClient {  // ← ELIMINADO @RequiredArgsConstructor

    private WebClient userWebClient;

    // Spring inyectará automáticamente este campo con @Qualifier
    public UserServiceClient(@Qualifier("userWebClient") WebClient userWebClient) {
        this.userWebClient = userWebClient;
    }

    public UserDTO getUser(Long userId) {
        return userWebClient.get()
                .uri("/users/{id}", userId)
                .header("X-Tenant-ID", String.valueOf(TenantContext.getTenantId()))
                .retrieve()
                .bodyToMono(UserDTO.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    public boolean isDriver(Long userId) {
        UserDTO user = getUser(userId);
        return user != null && "MOTORIZADO".equalsIgnoreCase(user.getRol());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String nombre;
        private String email;
        private String rol;
    }
}
