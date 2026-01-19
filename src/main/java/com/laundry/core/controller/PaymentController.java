package com.laundry.core.controller;

import com.laundry.core.dto.request.PaymentRequestDTO;
import com.laundry.core.dto.response.PaymentResponseDTO;
import com.laundry.core.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión de pagos de órdenes")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Registrar pago", description = "Registra un nuevo pago para una orden")
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentService.createPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/by-order/{orderId}")
    @Operation(summary = "Listar pagos por orden", description = "Obtiene todos los pagos de una orden")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByOrder(@PathVariable Long orderId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByOrder(orderId);
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pago", description = "Actualiza la información de un pago")
    public ResponseEntity<PaymentResponseDTO> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO payment = paymentService.updatePayment(id, request);
        return ResponseEntity.ok(payment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago", description = "Elimina un pago del sistema")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
