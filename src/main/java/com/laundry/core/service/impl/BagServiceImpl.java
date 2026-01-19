package com.laundry.core.service.impl;

import com.laundry.core.config.TenantContext;
import com.laundry.core.dto.request.BagAssignmentRequestDTO;
import com.laundry.core.dto.request.BagReleaseRequestDTO;
import com.laundry.core.dto.request.BagRequestDTO;
import com.laundry.core.dto.response.BagResponseDTO;
import com.laundry.core.entity.Bag;
import com.laundry.core.entity.BagAssignment;
import com.laundry.core.entity.Order;
import com.laundry.core.exception.ResourceNotFoundException;
import com.laundry.core.exception.ValidationException;
import com.laundry.core.repository.BagAssignmentRepository;
import com.laundry.core.repository.BagRepository;
import com.laundry.core.repository.OrderRepository;
import com.laundry.core.service.BagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BagServiceImpl implements BagService {

    private final BagRepository bagRepository;
    private final BagAssignmentRepository assignmentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public BagResponseDTO createBag(BagRequestDTO request) {
        if (!request.getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El tenant ID no coincide con el contexto");
        }

        if (bagRepository.existsByQrCode(request.getQrCode())) {
            throw new ValidationException("Ya existe una bolsa con el código QR: " + request.getQrCode());
        }

        Bag bag = Bag.builder()
                .tenantId(request.getTenantId())
                .qrCode(request.getQrCode())
                .disponible(request.getDisponible())
                .build();

        bag = bagRepository.save(bag);
        return mapToResponseDTO(bag);
    }

    @Override
    public List<BagResponseDTO> getBagsByTenant(Long tenantId) {
        if (!tenantId.equals(TenantContext.getTenantId())) {
            throw new ValidationException("El tenant ID no coincide con el contexto");
        }

        return bagRepository.findByTenantId(tenantId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BagResponseDTO getBagById(Long id) {
        Bag bag = bagRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Bolsa no encontrada con ID: " + id));
        return mapToResponseDTO(bag);
    }

    @Override
    @Transactional
    public BagResponseDTO updateBag(Long id, BagRequestDTO request) {
        Bag bag = bagRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Bolsa no encontrada con ID: " + id));

        if (!bag.getQrCode().equals(request.getQrCode()) && bagRepository.existsByQrCode(request.getQrCode())) {
            throw new ValidationException("Ya existe una bolsa con el código QR: " + request.getQrCode());
        }

        bag.setQrCode(request.getQrCode());
        bag.setDisponible(request.getDisponible());

        bag = bagRepository.save(bag);
        return mapToResponseDTO(bag);
    }

    @Override
    @Transactional
    public void deleteBag(Long id) {
        Bag bag = bagRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Bolsa no encontrada con ID: " + id));
        bagRepository.delete(bag);
    }

    @Override
    @Transactional
    public void assignBag(BagAssignmentRequestDTO request) {
        Bag bag = bagRepository.findByIdAndTenantId(request.getBagId(), TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Bolsa no encontrada"));

        Order order = orderRepository.findByIdAndTenantId(request.getOrderId(), TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if (!bag.getDisponible()) {
            throw new ValidationException("La bolsa no está disponible");
        }

        // Verificar si ya tiene asignación activa
        assignmentRepository.findByBagIdAndLiberadoEnIsNull(bag.getId())
                .ifPresent(assignment -> {
                    throw new ValidationException("La bolsa ya está asignada a otra orden");
                });

        BagAssignment assignment = BagAssignment.builder()
                .bag(bag)
                .order(order)
                .asignadoEn(LocalDateTime.now())
                .build();

        assignmentRepository.save(assignment);

        // Marcar bolsa como no disponible
        bag.setDisponible(false);
        bagRepository.save(bag);
    }

    @Override
    @Transactional
    public void releaseBag(BagReleaseRequestDTO request) {
        Bag bag = bagRepository.findByIdAndTenantId(request.getBagId(), TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Bolsa no encontrada"));

        BagAssignment assignment = assignmentRepository.findByBagIdAndLiberadoEnIsNull(bag.getId())
                .orElseThrow(() -> new ValidationException("La bolsa no tiene asignación activa"));

        assignment.setLiberadoEn(LocalDateTime.now());
        assignmentRepository.save(assignment);

        // Marcar bolsa como disponible
        bag.setDisponible(true);
        bagRepository.save(bag);
    }

    private BagResponseDTO mapToResponseDTO(Bag bag) {
        return BagResponseDTO.builder()
                .id(bag.getId())
                .tenantId(bag.getTenantId())
                .qrCode(bag.getQrCode())
                .disponible(bag.getDisponible())
                .createdAt(bag.getCreatedAt())
                .updatedAt(bag.getUpdatedAt())
                .build();
    }
}
