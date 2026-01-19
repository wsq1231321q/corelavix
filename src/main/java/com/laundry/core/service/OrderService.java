package com.laundry.core.service;

import com.laundry.core.dto.request.OrderRequestDTO;
import com.laundry.core.dto.request.OrderStatusUpdateDTO;
import com.laundry.core.dto.response.OrderResponseDTO;
import com.laundry.core.dto.response.OrderStatusHistoryResponseDTO;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO request);
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getOrdersByTenant(Long tenantId);
    OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO request);
    void deleteOrder(Long id);
    List<OrderStatusHistoryResponseDTO> getOrderHistory(Long orderId);
}
