package com.laundry.core.controller;

import com.laundry.core.dto.request.KardexRequestDTO;
import com.laundry.core.dto.response.KardexResponseDTO;
import com.laundry.core.service.KardexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/kardex")
@RequiredArgsConstructor
@Tag(name = "Kardex", description = "Gestión de costos reales por orden")
public class KardexController {

    private final KardexService kardexService;

    @PostMapping
    @Operation(summary = "Registrar costo", description = "Registra un costo real para una orden")
    public ResponseEntity<KardexResponseDTO> createKardex(@Valid @RequestBody KardexRequestDTO request) {
        KardexResponseDTO response = kardexService.createKardex(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/by-order/{orderId}")
    @Operation(summary = "Listar costos por orden", description = "Obtiene todos los costos registrados de una orden")
    public ResponseEntity<List<KardexResponseDTO>> getKardexByOrder(@PathVariable Long orderId) {
        List<KardexResponseDTO> kardex = kardexService.getKardexByOrder(orderId);
        return ResponseEntity.ok(kardex);
    }
}
