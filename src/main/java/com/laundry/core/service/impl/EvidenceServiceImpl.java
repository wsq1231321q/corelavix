package com.laundry.core.service.impl;

import com.laundry.core.config.TenantContext;
import com.laundry.core.dto.request.ItemEvidenceRequestDTO;
import com.laundry.core.dto.response.ItemEvidenceResponseDTO;
import com.laundry.core.entity.ItemEvidence;
import com.laundry.core.entity.OrderItem;
import com.laundry.core.exception.ResourceNotFoundException;
import com.laundry.core.exception.ValidationException;
import com.laundry.core.repository.ItemEvidenceRepository;
import com.laundry.core.repository.OrderItemRepository;
import com.laundry.core.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvidenceServiceImpl implements EvidenceService {

    private final ItemEvidenceRepository evidenceRepository;
    private final OrderItemRepository itemRepository;

    @Override
    @Transactional
    public ItemEvidenceResponseDTO createEvidence(ItemEvidenceRequestDTO request) {
        OrderItem item = itemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        if (!item.getOrder().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El item no pertenece al tenant actual");
        }

        ItemEvidence.EvidenceType tipo;
        try {
            tipo = ItemEvidence.EvidenceType.valueOf(request.getTipo());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Tipo de evidencia inválido: " + request.getTipo());
        }

        ItemEvidence evidence = ItemEvidence.builder()
                .orderItem(item)
                .tipo(tipo)
                .etiqueta(request.getEtiqueta())
                .fotoBase64(request.getFotoBase64())
                .geoLat(request.getGeoLat())
                .geoLng(request.getGeoLng())
                .creadoPor(request.getCreadoPor() != null ? request.getCreadoPor() : TenantContext.getUserId())
                .build();

        evidence = evidenceRepository.save(evidence);
        return mapToResponseDTO(evidence);
    }

    @Override
    public List<ItemEvidenceResponseDTO> getEvidencesByItem(Long itemId) {
        OrderItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        if (!item.getOrder().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El item no pertenece al tenant actual");
        }

        return evidenceRepository.findByOrderItemId(itemId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEvidence(Long id) {
        ItemEvidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia no encontrada"));

        if (!evidence.getOrderItem().getOrder().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("La evidencia no pertenece al tenant actual");
        }

        evidenceRepository.delete(evidence);
    }

    private ItemEvidenceResponseDTO mapToResponseDTO(ItemEvidence evidence) {
        return ItemEvidenceResponseDTO.builder()
                .id(evidence.getId())
                .orderItemId(evidence.getOrderItem().getId())
                .tipo(evidence.getTipo().name())
                .etiqueta(evidence.getEtiqueta())
                .fotoBase64(evidence.getFotoBase64())
                .geoLat(evidence.getGeoLat())
                .geoLng(evidence.getGeoLng())
                .creadoPor(evidence.getCreadoPor())
                .createdAt(evidence.getCreatedAt())
                .build();
    }
}
