package com.laundry.core.controller;

import com.laundry.core.dto.request.ItemEvidenceRequestDTO;
import com.laundry.core.dto.response.ItemEvidenceResponseDTO;
import com.laundry.core.service.EvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/evidence")
@RequiredArgsConstructor
@Tag(name = "Evidencia", description = "Gestión de evidencia fotográfica")
public class EvidenceController {

    private final EvidenceService evidenceService;

    @PostMapping
    @Operation(summary = "Registrar evidencia", description = "Registra evidencia fotográfica para una prenda")
    public ResponseEntity<ItemEvidenceResponseDTO> createEvidence(@Valid @RequestBody ItemEvidenceRequestDTO request) {
        ItemEvidenceResponseDTO response = evidenceService.createEvidence(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/by-item/{itemId}")
    @Operation(summary = "Listar evidencia por prenda", description = "Obtiene toda la evidencia de una prenda")
    public ResponseEntity<List<ItemEvidenceResponseDTO>> getEvidencesByItem(@PathVariable Long itemId) {
        List<ItemEvidenceResponseDTO> evidences = evidenceService.getEvidencesByItem(itemId);
        return ResponseEntity.ok(evidences);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evidencia", description = "Elimina una evidencia fotográfica")
    public ResponseEntity<Void> deleteEvidence(@PathVariable Long id) {
        evidenceService.deleteEvidence(id);
        return ResponseEntity.noContent().build();
    }
}
