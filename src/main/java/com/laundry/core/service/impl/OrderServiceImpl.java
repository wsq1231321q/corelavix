package com.laundry.core.service.impl;

import com.laundry.core.client.UserServiceClient;
import com.laundry.core.config.TenantContext;
import com.laundry.core.dto.request.OrderRequestDTO;
import com.laundry.core.dto.request.OrderStatusUpdateDTO;
import com.laundry.core.dto.response.OrderItemResponseDTO;
import com.laundry.core.dto.response.OrderResponseDTO;
import com.laundry.core.dto.response.OrderStatusHistoryResponseDTO;
import com.laundry.core.dto.response.PaymentResponseDTO;
import com.laundry.core.entity.Client;
import com.laundry.core.entity.Order;
import com.laundry.core.entity.OrderStatusHistory;
import com.laundry.core.exception.ResourceNotFoundException;
import com.laundry.core.exception.ValidationException;
import com.laundry.core.repository.ClientRepository;
import com.laundry.core.repository.OrderRepository;
import com.laundry.core.repository.OrderStatusHistoryRepository;
import com.laundry.core.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        if (!request.getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El tenant ID no coincide con el contexto");
        }

        Client client = clientRepository.findByIdAndTenantId(request.getClientId(), request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado o no pertenece al tenant"));

        Order order = Order.builder()
                .tenantId(request.getTenantId())
                .client(client)
                .estado(Order.OrderStatus.RECIBIDO)
                .fechaPrometida(request.getFechaPrometida())
                .total(BigDecimal.ZERO)
                .saldoPendiente(BigDecimal.ZERO)
                .build();

        order = orderRepository.save(order);

        // Registrar auditoría de creación
        registerStatusChange(order, null, Order.OrderStatus.RECIBIDO.name());

        return mapToResponseDTO(order);
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        return mapToResponseDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByTenant(Long tenantId) {
        if (!tenantId.equals(TenantContext.getTenantId())) {
            throw new ValidationException("El tenant ID no coincide con el contexto");
        }

        return orderRepository.findByTenantId(tenantId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO request) {
        Order order = orderRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        String estadoAnterior = order.getEstado().name();

        try {
            Order.OrderStatus nuevoEstado = Order.OrderStatus.valueOf(request.getEstado());
            order.setEstado(nuevoEstado);

            // Actualizar estados de items
            order.getItems().forEach(item -> {
                try {
                    item.setEstado(com.laundry.core.entity.OrderItem.ItemStatus.valueOf(request.getEstado()));
                } catch (IllegalArgumentException e) {
                    // Si el estado no existe en ItemStatus, mantener el actual
                }
            });

            order = orderRepository.save(order);

            // Registrar auditoría
            registerStatusChange(order, estadoAnterior, nuevoEstado.name(), request.getModificadoPor());

        } catch (IllegalArgumentException e) {
            throw new ValidationException("Estado inválido: " + request.getEstado());
        }

        return mapToResponseDTO(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        orderRepository.delete(order);
    }

    @Override
    public List<OrderStatusHistoryResponseDTO> getOrderHistory(Long orderId) {
        Order order = orderRepository.findByIdAndTenantId(orderId, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        return historyRepository.findByOrderIdOrderByModificadoEnDesc(orderId).stream()
                .map(this::mapToHistoryResponseDTO)
                .collect(Collectors.toList());
    }

    private void registerStatusChange(Order order, String estadoAnterior, String estadoNuevo) {
        registerStatusChange(order, estadoAnterior, estadoNuevo, TenantContext.getUserId());
    }

    private void registerStatusChange(Order order, String estadoAnterior, String estadoNuevo, Long modificadoPor) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .modificadoPor(modificadoPor)
                .modificadoEn(LocalDateTime.now())
                .build();

        historyRepository.save(history);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(item -> OrderItemResponseDTO.builder()
                        .id(item.getId())
                        .orderId(order.getId())
                        .serviceId(item.getServiceId())
                        .color(item.getColor())
                        .material(item.getMaterial())
                        .esDelicado(item.getEsDelicado())
                        .pesoKg(item.getPesoKg())
                        .estado(item.getEstado().name())
                        .bagId(item.getBag() != null ? item.getBag().getId() : null)
                        .bagQrCode(item.getBag() != null ? item.getBag().getQrCode() : null)
                        .precioUnitario(item.getPrecioUnitario())
                        .subtotal(item.getSubtotal())
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        List<PaymentResponseDTO> payments = order.getPayments().stream()
                .map(payment -> PaymentResponseDTO.builder()
                        .id(payment.getId())
                        .orderId(order.getId())
                        .monto(payment.getMonto())
                        .metodo(payment.getMetodo().name())
                        .estado(payment.getEstado().name())
                        .pagadoEn(payment.getPagadoEn())
                        .createdAt(payment.getCreatedAt())
                        .updatedAt(payment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .tenantId(order.getTenantId())
                .clientId(order.getClient().getId())
                .clienteNombre(order.getClient().getNombreCompleto())
                .estado(order.getEstado().name())
                .fechaPrometida(order.getFechaPrometida())
                .total(order.getTotal())
                .saldoPendiente(order.getSaldoPendiente())
                .items(items)
                .payments(payments)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderStatusHistoryResponseDTO mapToHistoryResponseDTO(OrderStatusHistory history) {
        String modificadoPorNombre = null;
        if (history.getModificadoPor() != null) {
            UserServiceClient.UserDTO user = userServiceClient.getUser(history.getModificadoPor());
            if (user != null) {
                modificadoPorNombre = user.getNombre();
            }
        }

        return OrderStatusHistoryResponseDTO.builder()
                .id(history.getId())
                .orderId(history.getOrder().getId())
                .estadoAnterior(history.getEstadoAnterior())
                .estadoNuevo(history.getEstadoNuevo())
                .modificadoPor(history.getModificadoPor())
                .modificadoPorNombre(modificadoPorNombre)
                .modificadoEn(history.getModificadoEn())
                .build();
    }
}
