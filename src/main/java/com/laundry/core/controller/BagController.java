package com.laundry.core.controller;

import com.laundry.core.dto.request.BagAssignmentRequestDTO;
import com.laundry.core.dto.request.BagReleaseRequestDTO;
import com.laundry.core.dto.request.BagRequestDTO;
import com.laundry.core.dto.response.BagResponseDTO;
import com.laundry.core.service.BagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bags")
@RequiredArgsConstructor
@Tag(name = "Bolsas", description = "Gestión de bolsas con código QR")
public class BagController {

    private final BagService bagService;

    @PostMapping
    @Operation(summary = "Crear bolsa", description = "Registra una nueva bolsa en el sistema")
    public ResponseEntity<BagResponseDTO> createBag(@Valid @RequestBody BagRequestDTO request) {
        BagResponseDTO response = bagService.createBag(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/by-tenant/{tenantId}")
    @Operation(summary = "Listar bolsas por tenant", description = "Obtiene todas las bolsas de un tenant")
    public ResponseEntity<List<BagResponseDTO>> getBagsByTenant(@PathVariable Long tenantId) {
        List<BagResponseDTO> bags = bagService.getBagsByTenant(tenantId);
        return ResponseEntity.ok(bags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener bolsa por ID", description = "Obtiene la información de una bolsa específica")
    public ResponseEntity<BagResponseDTO> getBagById(@PathVariable Long id) {
        BagResponseDTO bag = bagService.getBagById(id);
        return ResponseEntity.ok(bag);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar bolsa", description = "Actualiza la información de una bolsa")
    public ResponseEntity<BagResponseDTO> updateBag(
            @PathVariable Long id,
            @Valid @RequestBody BagRequestDTO request) {
        BagResponseDTO bag = bagService.updateBag(id, request);
        return ResponseEntity.ok(bag);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar bolsa", description = "Elimina una bolsa del sistema")
    public ResponseEntity<Void> deleteBag(@PathVariable Long id) {
        bagService.deleteBag(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    @Operation(summary = "Asignar bolsa", description = "Asigna una bolsa a una orden")
    public ResponseEntity<Void> assignBag(@Valid @RequestBody BagAssignmentRequestDTO request) {
        bagService.assignBag(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    @Operation(summary = "Liberar bolsa", description = "Libera una bolsa para que esté disponible nuevamente")
    public ResponseEntity<Void> releaseBag(@Valid @RequestBody BagReleaseRequestDTO request) {
        bagService.releaseBag(request);
        return ResponseEntity.ok().build();
    }
}
