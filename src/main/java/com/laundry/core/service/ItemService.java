package com.laundry.core.service;

import com.laundry.core.dto.request.OrderItemRequestDTO;
import com.laundry.core.dto.response.OrderItemResponseDTO;
import java.util.List;

public interface ItemService {
    OrderItemResponseDTO createItem(OrderItemRequestDTO request);
    List<OrderItemResponseDTO> getItemsByOrder(Long orderId);
    OrderItemResponseDTO getItemById(Long id);
    OrderItemResponseDTO updateItem(Long id, OrderItemRequestDTO request);
    void deleteItem(Long id);
}
