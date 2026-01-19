package com.laundry.core.controller;

import com.laundry.core.dto.request.OrderItemRequestDTO;
import com.laundry.core.dto.response.OrderItemResponseDTO;
import com.laundry.core.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Prendas", description = "Gestión de prendas por orden")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "Agregar prenda", description = "Registra una nueva prenda en una orden")
    public ResponseEntity<OrderItemResponseDTO> createItem(@Valid @RequestBody OrderItemRequestDTO request) {
        OrderItemResponseDTO response = itemService.createItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/by-order/{orderId}")
    @Operation(summary = "Listar prendas por orden", description = "Obtiene todas las prendas de una orden")
    public ResponseEntity<List<OrderItemResponseDTO>> getItemsByOrder(@PathVariable Long orderId) {
        List<OrderItemResponseDTO> items = itemService.getItemsByOrder(orderId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener prenda por ID", description = "Obtiene la información de una prenda específica")
    public ResponseEntity<OrderItemResponseDTO> getItemById(@PathVariable Long id) {
        OrderItemResponseDTO item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar prenda", description = "Actualiza la información de una prenda")
    public ResponseEntity<OrderItemResponseDTO> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemRequestDTO request) {
        OrderItemResponseDTO item = itemService.updateItem(id, request);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar prenda", description = "Elimina una prenda de una orden")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
