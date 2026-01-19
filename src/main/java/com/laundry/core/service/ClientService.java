package com.laundry.core.service;

import com.laundry.core.dto.request.ClientAddressRequestDTO;
import com.laundry.core.dto.request.ClientRequestDTO;
import com.laundry.core.dto.response.ClientAddressResponseDTO;
import com.laundry.core.dto.response.ClientResponseDTO;
import java.util.List;

public interface ClientService {
    ClientResponseDTO createClient(ClientRequestDTO request);
    ClientResponseDTO getClientById(Long id);
    List<ClientResponseDTO> getClientsByTenant(Long tenantId);
    ClientResponseDTO updateClient(Long id, ClientRequestDTO request);
    void deleteClient(Long id);
    ClientAddressResponseDTO addAddress(ClientAddressRequestDTO request);
    List<ClientAddressResponseDTO> getAddressesByClient(Long clientId);
    ClientAddressResponseDTO updateAddress(Long id, ClientAddressRequestDTO request);
    void deleteAddress(Long id);
}
