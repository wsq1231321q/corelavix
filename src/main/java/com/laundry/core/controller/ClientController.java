package com.laundry.core.controller;

import com.laundry.core.dto.request.ClientAddressRequestDTO;
import com.laundry.core.dto.request.ClientRequestDTO;
import com.laundry.core.dto.response.ClientAddressResponseDTO;
import com.laundry.core.dto.response.ClientResponseDTO;
import com.laundry.core.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes y direcciones")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @Operation(summary = "Crear cliente", description = "Registra un nuevo cliente en el sistema")
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientRequestDTO request) {
        ClientResponseDTO response = clientService.createClient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/by-tenant/{tenantId}")
    @Operation(summary = "Listar clientes por tenant", description = "Obtiene todos los clientes de un tenant")
    public ResponseEntity<List<ClientResponseDTO>> getClientsByTenant(@PathVariable Long tenantId) {
        List<ClientResponseDTO> clients = clientService.getClientsByTenant(tenantId);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", description = "Obtiene la información de un cliente específico")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza la información de un cliente")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDTO request) {
        ClientResponseDTO client = clientService.updateClient(id, request);
        return ResponseEntity.ok(client);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{clientId}/addresses")
    @Operation(summary = "Agregar dirección", description = "Agrega una nueva dirección a un cliente")
    public ResponseEntity<ClientAddressResponseDTO> addAddress(
            @PathVariable Long clientId,
            @Valid @RequestBody ClientAddressRequestDTO request) {
        request.setClientId(clientId);
        ClientAddressResponseDTO address = clientService.addAddress(request);
        return new ResponseEntity<>(address, HttpStatus.CREATED);
    }

    @GetMapping("/{clientId}/addresses")
    @Operation(summary = "Listar direcciones", description = "Obtiene todas las direcciones de un cliente")
    public ResponseEntity<List<ClientAddressResponseDTO>> getAddressesByClient(@PathVariable Long clientId) {
        List<ClientAddressResponseDTO> addresses = clientService.getAddressesByClient(clientId);
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Actualizar dirección", description = "Actualiza una dirección existente")
    public ResponseEntity<ClientAddressResponseDTO> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody ClientAddressRequestDTO request) {
        ClientAddressResponseDTO address = clientService.updateAddress(id, request);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Eliminar dirección", description = "Elimina una dirección del sistema")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        clientService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
