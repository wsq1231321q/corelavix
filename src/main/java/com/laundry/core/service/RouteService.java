package com.laundry.core.service;

import com.laundry.core.dto.request.RouteOrderRequestDTO;
import com.laundry.core.dto.request.RouteRequestDTO;
import com.laundry.core.dto.response.RouteResponseDTO;
import java.util.List;

public interface RouteService {
    RouteResponseDTO createRoute(RouteRequestDTO request);
    List<RouteResponseDTO> getRoutesByTenant(Long tenantId);
    RouteResponseDTO getRouteById(Long id);
    RouteResponseDTO updateRoute(Long id, RouteRequestDTO request);
    void deleteRoute(Long id);
    void assignOrderToRoute(RouteOrderRequestDTO request);
}
