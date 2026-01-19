package com.laundry.core.service.impl;

import com.laundry.core.config.TenantContext;
import com.laundry.core.dto.request.KardexRequestDTO;
import com.laundry.core.dto.response.KardexResponseDTO;
import com.laundry.core.entity.Kardex;
import com.laundry.core.entity.Order;
import com.laundry.core.exception.ResourceNotFoundException;
import com.laundry.core.exception.ValidationException;
import com.laundry.core.repository.KardexRepository;
import com.laundry.core.repository.OrderRepository;
import com.laundry.core.service.KardexService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KardexServiceImpl implements KardexService {

    private final KardexRepository kardexRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public KardexResponseDTO createKardex(KardexRequestDTO request) {
        Order order = orderRepository.findByIdAndTenantId(request.getOrderId(), TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        Kardex.KardexType tipo;
        try {
            tipo = Kardex.KardexType.valueOf(request.getTipo());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Tipo de costo inválido: " + request.getTipo());
        }

        Kardex kardex = Kardex.builder()
                .order(order)
                .tipo(tipo)
                .costo(request.getCosto())
                .build();

        kardex = kardexRepository.save(kardex);
        return mapToResponseDTO(kardex);
    }

    @Override
    public List<KardexResponseDTO> getKardexByOrder(Long orderId) {
        Order order = orderRepository.findByIdAndTenantId(orderId, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        return kardexRepository.findByOrderId(orderId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private KardexResponseDTO mapToResponseDTO(Kardex kardex) {
        return KardexResponseDTO.builder()
                .id(kardex.getId())
                .orderId(kardex.getOrder().getId())
                .tipo(kardex.getTipo().name())
                .costo(kardex.getCosto())
                .createdAt(kardex.getCreatedAt())
                .build();
    }
}
