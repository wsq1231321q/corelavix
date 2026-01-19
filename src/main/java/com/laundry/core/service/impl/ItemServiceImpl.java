package com.laundry.core.service.impl;

import com.laundry.core.client.ProductServiceClient;
import com.laundry.core.config.TenantContext;
import com.laundry.core.dto.request.OrderItemRequestDTO;
import com.laundry.core.dto.response.ItemEvidenceResponseDTO;
import com.laundry.core.dto.response.OrderItemResponseDTO;
import com.laundry.core.entity.Bag;
import com.laundry.core.entity.Order;
import com.laundry.core.entity.OrderItem;
import com.laundry.core.exception.ResourceNotFoundException;
import com.laundry.core.exception.ValidationException;
import com.laundry.core.repository.BagRepository;
import com.laundry.core.repository.OrderItemRepository;
import com.laundry.core.repository.OrderRepository;
import com.laundry.core.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final OrderItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final BagRepository bagRepository;
    private final ProductServiceClient productServiceClient;

    @Override
    @Transactional
    public OrderItemResponseDTO createItem(OrderItemRequestDTO request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if (!order.getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("La orden no pertenece al tenant actual");
        }

        // Validar servicio existe en Product Service
        ProductServiceClient.ServiceDTO service = productServiceClient.getService(request.getServiceId());
        if (service == null) {
            throw new ResourceNotFoundException("Servicio no encontrado con ID: " + request.getServiceId());
        }

        // Obtener precio activo
        ProductServiceClient.PrecioDTO precio = productServiceClient.getActivePrice(request.getServiceId());
        if (precio == null) {
            throw new ResourceNotFoundException("No hay precio activo para el servicio con ID: " + request.getServiceId());
        }

        // Obtener reglas del servicio
        ProductServiceClient.ReglaDTO regla = productServiceClient.getServiceRules(request.getServiceId());

        // Validar reglas
        if (regla != null && regla.getRequiresWeight() != null && regla.getRequiresWeight()) {
            if (request.getPesoKg() == null || request.getPesoKg().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("El servicio requiere especificar el peso");
            }

            if (regla.getMinWeight() != null && request.getPesoKg().compareTo(regla.getMinWeight()) < 0) {
                throw new ValidationException("El peso mínimo requerido es " + regla.getMinWeight() + " kg");
            }
        }

        // Calcular subtotal según tipo de precio
        BigDecimal precioUnitario = precio.getPrecio();
        BigDecimal subtotal;

        if ("POR_PESO".equalsIgnoreCase(precio.getPricingType())) {
            if (request.getPesoKg() == null) {
                throw new ValidationException("El servicio con precio por peso requiere especificar el peso");
            }
            subtotal = precioUnitario.multiply(request.getPesoKg());
        } else {
            // FIJO
            subtotal = precioUnitario;
        }

        // Aplicar cargo extra si existe
        if (regla != null && regla.getExtraCharge() != null && regla.getExtraCharge().compareTo(BigDecimal.ZERO) > 0) {
            subtotal = subtotal.add(regla.getExtraCharge());
        }

        // Crear item
        OrderItem item = OrderItem.builder()
                .order(order)
                .serviceId(request.getServiceId())
                .color(request.getColor())
                .material(request.getMaterial())
                .esDelicado(request.getEsDelicado())
                .pesoKg(request.getPesoKg())
                .estado(OrderItem.ItemStatus.RECIBIDO)
                .precioUnitario(precioUnitario)
                .subtotal(subtotal)
                .build();

        // Asignar bolsa si se especifica
        if (request.getBagId() != null) {
            Bag bag = bagRepository.findById(request.getBagId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bolsa no encontrada"));

            if (!bag.getTenantId().equals(TenantContext.getTenantId())) {
                throw new ValidationException("La bolsa no pertenece al tenant actual");
            }

            if (!bag.getDisponible()) {
                throw new ValidationException("La bolsa no está disponible");
            }

            item.setBag(bag);
        }

        item = itemRepository.save(item);

        // Actualizar total de la orden
        updateOrderTotal(order);

        return mapToResponseDTO(item, service.getNombre());
    }

    @Override
    public List<OrderItemResponseDTO> getItemsByOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if (!order.getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("La orden no pertenece al tenant actual");
        }

        return itemRepository.findByOrderId(orderId).stream()
                .map(item -> {
                    ProductServiceClient.ServiceDTO service = productServiceClient.getService(item.getServiceId());
                    return mapToResponseDTO(item, service != null ? service.getNombre() : "Servicio no disponible");
                })
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemResponseDTO getItemById(Long id) {
        OrderItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        if (!item.getOrder().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El item no pertenece al tenant actual");
        }

        ProductServiceClient.ServiceDTO service = productServiceClient.getService(item.getServiceId());
        return mapToResponseDTO(item, service != null ? service.getNombre() : "Servicio no disponible");
    }

    @Override
    @Transactional
    public OrderItemResponseDTO updateItem(Long id, OrderItemRequestDTO request) {
        OrderItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        if (!item.getOrder().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El item no pertenece al tenant actual");
        }

        // Validar y recalcular si cambia el servicio
        if (!item.getServiceId().equals(request.getServiceId())) {
            ProductServiceClient.ServiceDTO service = productServiceClient.getService(request.getServiceId());
            if (service == null) {
                throw new ResourceNotFoundException("Servicio no encontrado");
            }

            ProductServiceClient.PrecioDTO precio = productServiceClient.getActivePrice(request.getServiceId());
            if (precio == null) {
                throw new ResourceNotFoundException("No hay precio activo para el servicio");
            }

            item.setServiceId(request.getServiceId());
            item.setPrecioUnitario(precio.getPrecio());

            // Recalcular subtotal
            if ("POR_PESO".equalsIgnoreCase(precio.getPricingType())) {
                if (request.getPesoKg() == null) {
                    throw new ValidationException("El servicio requiere peso");
                }
                item.setSubtotal(precio.getPrecio().multiply(request.getPesoKg()));
            } else {
                item.setSubtotal(precio.getPrecio());
            }
        }

        item.setColor(request.getColor());
        item.setMaterial(request.getMaterial());
        item.setEsDelicado(request.getEsDelicado());
        item.setPesoKg(request.getPesoKg());

        // Actualizar bolsa si cambió
        if (request.getBagId() != null && !request.getBagId().equals(item.getBag() != null ? item.getBag().getId() : null)) {
            Bag bag = bagRepository.findById(request.getBagId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bolsa no encontrada"));

            if (!bag.getTenantId().equals(TenantContext.getTenantId())) {
                throw new ValidationException("La bolsa no pertenece al tenant actual");
            }

            if (!bag.getDisponible()) {
                throw new ValidationException("La bolsa no está disponible");
            }

            item.setBag(bag);
        }

        item = itemRepository.save(item);

        // Actualizar total de la orden
        updateOrderTotal(item.getOrder());

        ProductServiceClient.ServiceDTO service = productServiceClient.getService(item.getServiceId());
        return mapToResponseDTO(item, service != null ? service.getNombre() : "Servicio no disponible");
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        OrderItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        if (!item.getOrder().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El item no pertenece al tenant actual");
        }

        Order order = item.getOrder();
        itemRepository.delete(item);

        // Actualizar total de la orden
        updateOrderTotal(order);
    }

    private void updateOrderTotal(Order order) {
        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);

        // Recalcular saldo pendiente
        BigDecimal totalPagado = order.getPayments().stream()
                .filter(p -> p.getEstado() == com.laundry.core.entity.Payment.PaymentStatus.PAGADO)
                .map(com.laundry.core.entity.Payment::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setSaldoPendiente(total.subtract(totalPagado));

        orderRepository.save(order);
    }

    private OrderItemResponseDTO mapToResponseDTO(OrderItem item, String servicioNombre) {
        List<ItemEvidenceResponseDTO> evidences = item.getEvidences().stream()
                .map(evidence -> ItemEvidenceResponseDTO.builder()
                        .id(evidence.getId())
                        .orderItemId(item.getId())
                        .tipo(evidence.getTipo().name())
                        .etiqueta(evidence.getEtiqueta())
                        .fotoBase64(evidence.getFotoBase64())
                        .geoLat(evidence.getGeoLat())
                        .geoLng(evidence.getGeoLng())
                        .creadoPor(evidence.getCreadoPor())
                        .createdAt(evidence.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .serviceId(item.getServiceId())
                .servicioNombre(servicioNombre)
                .color(item.getColor())
                .material(item.getMaterial())
                .esDelicado(item.getEsDelicado())
                .pesoKg(item.getPesoKg())
                .estado(item.getEstado().name())
                .bagId(item.getBag() != null ? item.getBag().getId() : null)
                .bagQrCode(item.getBag() != null ? item.getBag().getQrCode() : null)
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .evidences(evidences)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
