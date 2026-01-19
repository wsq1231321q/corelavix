package com.laundry.core.service.impl;

import com.laundry.core.config.TenantContext;
import com.laundry.core.dto.request.PaymentRequestDTO;
import com.laundry.core.dto.response.PaymentResponseDTO;
import com.laundry.core.entity.Order;
import com.laundry.core.entity.Payment;
import com.laundry.core.exception.ResourceNotFoundException;
import com.laundry.core.exception.ValidationException;
import com.laundry.core.repository.OrderRepository;
import com.laundry.core.repository.PaymentRepository;
import com.laundry.core.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        Order order = orderRepository.findByIdAndTenantId(request.getOrderId(), TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        // Validar que el monto no exceda el saldo pendiente
        if (request.getMonto().compareTo(order.getSaldoPendiente()) > 0) {
            throw new ValidationException("El monto del pago (" + request.getMonto() +
                    ") excede el saldo pendiente (" + order.getSaldoPendiente() + ")");
        }

        Payment.PaymentMethod metodo;
        try {
            metodo = Payment.PaymentMethod.valueOf(request.getMetodo());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Método de pago inválido: " + request.getMetodo());
        }

        Payment payment = Payment.builder()
                .order(order)
                .monto(request.getMonto())
                .metodo(metodo)
                .estado(Payment.PaymentStatus.PAGADO)
                .pagadoEn(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        // Actualizar saldo pendiente de la orden
        updateOrderBalance(order);

        return mapToResponseDTO(payment);
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByOrder(Long orderId) {
        Order order = orderRepository.findByIdAndTenantId(orderId, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePayment(Long id, PaymentRequestDTO request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        if (!payment.getOrder().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El pago no pertenece al tenant actual");
        }

        Order order = payment.getOrder();

        // Validar nuevo monto
        BigDecimal saldoSinEstePago = order.getSaldoPendiente().add(payment.getMonto());
        if (request.getMonto().compareTo(saldoSinEstePago) > 0) {
            throw new ValidationException("El nuevo monto excede el saldo disponible");
        }


        payment.setMonto(request.getMonto());

        try {
            payment.setMetodo(Payment.PaymentMethod.valueOf(request.getMetodo()));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Método de pago inválido: " + request.getMetodo());
        }

        payment = paymentRepository.save(payment);

        // Actualizar saldo pendiente de la orden
        updateOrderBalance(order);

        return mapToResponseDTO(payment);
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        if (!payment.getOrder().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El pago no pertenece al tenant actual");
        }

        Order order = payment.getOrder();
        paymentRepository.delete(payment);

        // Actualizar saldo pendiente de la orden
        updateOrderBalance(order);
    }

    private void updateOrderBalance(Order order) {
        BigDecimal totalPagado = order.getPayments().stream()
                .filter(p -> p.getEstado() == Payment.PaymentStatus.PAGADO)
                .map(Payment::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setSaldoPendiente(order.getTotal().subtract(totalPagado));
        orderRepository.save(order);
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .monto(payment.getMonto())
                .metodo(payment.getMetodo().name())
                .estado(payment.getEstado().name())
                .pagadoEn(payment.getPagadoEn())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
