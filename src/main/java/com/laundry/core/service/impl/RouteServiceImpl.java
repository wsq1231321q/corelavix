package com.laundry.core.service.impl;

import com.laundry.core.client.UserServiceClient;
import com.laundry.core.config.TenantContext;
import com.laundry.core.dto.request.RouteOrderRequestDTO;
import com.laundry.core.dto.request.RouteRequestDTO;
import com.laundry.core.dto.response.RouteOrderResponseDTO;
import com.laundry.core.dto.response.RouteResponseDTO;
import com.laundry.core.entity.Order;
import com.laundry.core.entity.Route;
import com.laundry.core.entity.RouteOrder;
import com.laundry.core.exception.ResourceNotFoundException;
import com.laundry.core.exception.ValidationException;
import com.laundry.core.repository.OrderRepository;
import com.laundry.core.repository.RouteOrderRepository;
import com.laundry.core.repository.RouteRepository;
import com.laundry.core.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteOrderRepository routeOrderRepository;
    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public RouteResponseDTO createRoute(RouteRequestDTO request) {
        if (!request.getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El tenant ID no coincide con el contexto");
        }

        // Validar que el driver existe y tiene rol MOTORIZADO
        if (!userServiceClient.isDriver(request.getDriverId())) {
            throw new ValidationException("El usuario no es un motorizado válido o no existe");
        }

        Route route = Route.builder()
                .tenantId(request.getTenantId())
                .driverId(request.getDriverId())
                .estado(Route.RouteStatus.CREADO)
                .build();

        route = routeRepository.save(route);
        return mapToResponseDTO(route);
    }

    @Override
    public List<RouteResponseDTO> getRoutesByTenant(Long tenantId) {
        if (!tenantId.equals(TenantContext.getTenantId())) {
            throw new ValidationException("El tenant ID no coincide con el contexto");
        }

        return routeRepository.findByTenantId(tenantId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RouteResponseDTO getRouteById(Long id) {
        Route route = routeRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada con ID: " + id));
        return mapToResponseDTO(route);
    }

    @Override
    @Transactional
    public RouteResponseDTO updateRoute(Long id, RouteRequestDTO request) {
        Route route = routeRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada con ID: " + id));

        // Validar nuevo driver si cambió
        if (!route.getDriverId().equals(request.getDriverId())) {
            if (!userServiceClient.isDriver(request.getDriverId())) {
                throw new ValidationException("El usuario no es un motorizado válido o no existe");
            }
            route.setDriverId(request.getDriverId());
        }

        route = routeRepository.save(route);
        return mapToResponseDTO(route);
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        Route route = routeRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada con ID: " + id));
        routeRepository.delete(route);
    }

    @Override
    @Transactional
    public void assignOrderToRoute(RouteOrderRequestDTO request) {
        Route route = routeRepository.findByIdAndTenantId(request.getRouteId(), TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada"));

        Order order = orderRepository.findByIdAndTenantId(request.getOrderId(), TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        RouteOrder routeOrder = RouteOrder.builder()
                .route(route)
                .order(order)
                .ordenDeParada(request.getOrdenDeParada())
                .build();

        routeOrderRepository.save(routeOrder);

        // Actualizar estado de la orden a EN_RUTA
        order.setEstado(Order.OrderStatus.EN_RUTA);
        orderRepository.save(order);
    }

    private RouteResponseDTO mapToResponseDTO(Route route) {
        UserServiceClient.UserDTO driver = userServiceClient.getUser(route.getDriverId());

        List<RouteOrderResponseDTO> routeOrders = route.getRouteOrders().stream()
                .map(ro -> RouteOrderResponseDTO.builder()
                        .id(ro.getId())
                        .routeId(route.getId())
                        .orderId(ro.getOrder().getId())
                        .ordenNumero("ORD-" + ro.getOrder().getId())
                        .clienteNombre(ro.getOrder().getClient().getNombreCompleto())
                        .direccion(ro.getOrder().getClient().getAddresses().isEmpty() ?
                                "Sin dirección" :
                                ro.getOrder().getClient().getAddresses().get(0).getDireccion())
                        .ordenDeParada(ro.getOrdenDeParada())
                        .build())
                .collect(Collectors.toList());

        return RouteResponseDTO.builder()
                .id(route.getId())
                .tenantId(route.getTenantId())
                .driverId(route.getDriverId())
                .driverNombre(driver != null ? driver.getNombre() : "Driver no disponible")
                .estado(route.getEstado().name())
                .routeOrders(routeOrders)
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .build();
    }
}
