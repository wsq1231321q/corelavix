package com.laundry.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Core Service API - Lavandería Multi-Tenant")
                        .version("1.0.0")
                        .description("API REST para gestión de órdenes, clientes, pagos y delivery en lavandería SaaS"));
    }

    @Bean
    public OperationCustomizer customizeOperation() {
        return (operation, handlerMethod) -> {
            Parameter tenantIdHeader = new Parameter()
                    .in("header")
                    .required(true)
                    .name("X-Tenant-ID")
                    .description("ID del tenant")
                    .schema(new StringSchema());

            Parameter userIdHeader = new Parameter()
                    .in("header")
                    .required(false)
                    .name("X-User-ID")
                    .description("ID del usuario (opcional)")
                    .schema(new StringSchema());

            operation.addParametersItem(tenantIdHeader);
            operation.addParametersItem(userIdHeader);

            return operation;
        };
    }
}
