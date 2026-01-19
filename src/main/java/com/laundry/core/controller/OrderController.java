package com.laundry.core.controller;

import com.laundry.core.dto.request.OrderRequestDTO;
import com.laundry.core.dto.request.OrderStatusUpdateDTO;
import com.laundry.core.dto.response.OrderResponseDTO;
import com.laundry.core.dto.response.OrderStatusHistoryResponseDTO;
import com.laundry.core.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Órdenes", description = "Gestión de órdenes de lavandería")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Crear orden", description = "Registra una nueva orden de lavandería")
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        OrderResponseDTO response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID", description = "Obtiene la información completa de una orden")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/by-tenant/{tenantId}")
    @Operation(summary = "Listar órdenes por tenant", description = "Obtiene todas las órdenes de un tenant")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByTenant(@PathVariable Long tenantId) {
        List<OrderResponseDTO> orders = orderService.getOrdersByTenant(tenantId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cambiar estado de orden", description = "Actualiza el estado de una orden y registra auditoría")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDTO request) {
        OrderResponseDTO order = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar orden", description = "Elimina una orden del sistema")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}/history")
    @Operation(summary = "Historial de cambios", description = "Obtiene el historial de cambios de estado de una orden")
    public ResponseEntity<List<OrderStatusHistoryResponseDTO>> getOrderHistory(@PathVariable Long orderId) {
        List<OrderStatusHistoryResponseDTO> history = orderService.getOrderHistory(orderId);
        return ResponseEntity.ok(history);
    }
}
