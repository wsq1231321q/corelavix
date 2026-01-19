package com.laundry.core.controller;

import com.laundry.core.dto.request.RouteOrderRequestDTO;
import com.laundry.core.dto.request.RouteRequestDTO;
import com.laundry.core.dto.response.RouteResponseDTO;
import com.laundry.core.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name = "Rutas de Delivery", description = "Gestión de rutas y asignación de órdenes")
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    @Operation(summary = "Crear ruta", description = "Crea una nueva ruta de delivery con un motorizado")
    public ResponseEntity<RouteResponseDTO> createRoute(@Valid @RequestBody RouteRequestDTO request) {
        RouteResponseDTO response = routeService.createRoute(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/by-tenant/{tenantId}")
    @Operation(summary = "Listar rutas por tenant", description = "Obtiene todas las rutas de un tenant")
    public ResponseEntity<List<RouteResponseDTO>> getRoutesByTenant(@PathVariable Long tenantId) {
        List<RouteResponseDTO> routes = routeService.getRoutesByTenant(tenantId);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ruta por ID", description = "Obtiene la información completa de una ruta")
    public ResponseEntity<RouteResponseDTO> getRouteById(@PathVariable Long id) {
        RouteResponseDTO route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ruta", description = "Actualiza la información de una ruta")
    public ResponseEntity<RouteResponseDTO> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody RouteRequestDTO request) {
        RouteResponseDTO route = routeService.updateRoute(id, request);
        return ResponseEntity.ok(route);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ruta", description = "Elimina una ruta del sistema")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    @Operation(summary = "Asignar orden a ruta", description = "Asigna una orden a una ruta de delivery")
    public ResponseEntity<Void> assignOrderToRoute(@Valid @RequestBody RouteOrderRequestDTO request) {
        routeService.assignOrderToRoute(request);
        return ResponseEntity.ok().build();
    }
}
