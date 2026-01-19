package com.laundry.core.service;

import com.laundry.core.dto.request.ItemEvidenceRequestDTO;
import com.laundry.core.dto.response.ItemEvidenceResponseDTO;
import java.util.List;

public interface EvidenceService {
    ItemEvidenceResponseDTO createEvidence(ItemEvidenceRequestDTO request);
    List<ItemEvidenceResponseDTO> getEvidencesByItem(Long itemId);
    void deleteEvidence(Long id);
}
