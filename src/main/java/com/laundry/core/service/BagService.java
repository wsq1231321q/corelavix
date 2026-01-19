package com.laundry.core.service;

import com.laundry.core.dto.request.BagAssignmentRequestDTO;
import com.laundry.core.dto.request.BagReleaseRequestDTO;
import com.laundry.core.dto.request.BagRequestDTO;
import com.laundry.core.dto.response.BagResponseDTO;
import java.util.List;

public interface BagService {
    BagResponseDTO createBag(BagRequestDTO request);
    List<BagResponseDTO> getBagsByTenant(Long tenantId);
    BagResponseDTO getBagById(Long id);
    BagResponseDTO updateBag(Long id, BagRequestDTO request);
    void deleteBag(Long id);
    void assignBag(BagAssignmentRequestDTO request);
    void releaseBag(BagReleaseRequestDTO request);
}
