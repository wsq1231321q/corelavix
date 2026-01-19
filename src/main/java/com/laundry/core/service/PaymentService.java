package com.laundry.core.service;

import com.laundry.core.dto.request.PaymentRequestDTO;
import com.laundry.core.dto.response.PaymentResponseDTO;
import java.util.List;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO request);
    List<PaymentResponseDTO> getPaymentsByOrder(Long orderId);
    PaymentResponseDTO updatePayment(Long id, PaymentRequestDTO request);
    void deletePayment(Long id);
}
