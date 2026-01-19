package com.laundry.core.service;

import com.laundry.core.dto.request.KardexRequestDTO;
import com.laundry.core.dto.response.KardexResponseDTO;
import java.util.List;

public interface KardexService {
    KardexResponseDTO createKardex(KardexRequestDTO request);
    List<KardexResponseDTO> getKardexByOrder(Long orderId);
}
