package com.laundry.core.service.impl;

import com.laundry.core.config.TenantContext;
import com.laundry.core.dto.request.ClientAddressRequestDTO;
import com.laundry.core.dto.request.ClientRequestDTO;
import com.laundry.core.dto.response.ClientAddressResponseDTO;
import com.laundry.core.dto.response.ClientResponseDTO;
import com.laundry.core.entity.Client;
import com.laundry.core.entity.ClientAddress;
import com.laundry.core.exception.ResourceNotFoundException;
import com.laundry.core.exception.ValidationException;
import com.laundry.core.repository.ClientAddressRepository;
import com.laundry.core.repository.ClientRepository;
import com.laundry.core.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientAddressRepository addressRepository;

    @Override
    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO request) {
        if (!request.getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El tenant ID no coincide con el contexto");
        }

        Client client = Client.builder()
                .tenantId(request.getTenantId())
                .nombreCompleto(request.getNombreCompleto())
                .telefono(request.getTelefono())
                .whatsapp(request.getWhatsapp())
                .email(request.getEmail())
                .esVip(request.getEsVip())
                .build();

        client = clientRepository.save(client);
        return mapToResponseDTO(client);
    }

    @Override
    public ClientResponseDTO getClientById(Long id) {
        Client client = clientRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        return mapToResponseDTO(client);
    }

    @Override
    public List<ClientResponseDTO> getClientsByTenant(Long tenantId) {
        if (!tenantId.equals(TenantContext.getTenantId())) {
            throw new ValidationException("El tenant ID no coincide con el contexto");
        }

        return clientRepository.findByTenantId(tenantId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO request) {
        Client client = clientRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        client.setNombreCompleto(request.getNombreCompleto());
        client.setTelefono(request.getTelefono());
        client.setWhatsapp(request.getWhatsapp());
        client.setEmail(request.getEmail());
        client.setEsVip(request.getEsVip());

        client = clientRepository.save(client);
        return mapToResponseDTO(client);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        clientRepository.delete(client);
    }

    @Override
    @Transactional
    public ClientAddressResponseDTO addAddress(ClientAddressRequestDTO request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (!client.getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El cliente no pertenece al tenant actual");
        }

        ClientAddress address = ClientAddress.builder()
                .client(client)
                .etiqueta(request.getEtiqueta())
                .direccion(request.getDireccion())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .build();

        address = addressRepository.save(address);
        return mapToAddressResponseDTO(address);
    }

    @Override
    public List<ClientAddressResponseDTO> getAddressesByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (!client.getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("El cliente no pertenece al tenant actual");
        }

        return addressRepository.findByClientId(clientId).stream()
                .map(this::mapToAddressResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClientAddressResponseDTO updateAddress(Long id, ClientAddressRequestDTO request) {
        ClientAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));

        if (!address.getClient().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("La dirección no pertenece al tenant actual");
        }

        address.setEtiqueta(request.getEtiqueta());
        address.setDireccion(request.getDireccion());
        address.setLatitud(request.getLatitud());
        address.setLongitud(request.getLongitud());

        address = addressRepository.save(address);
        return mapToAddressResponseDTO(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        ClientAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));

        if (!address.getClient().getTenantId().equals(TenantContext.getTenantId())) {
            throw new ValidationException("La dirección no pertenece al tenant actual");
        }

        addressRepository.delete(address);
    }

    private ClientResponseDTO mapToResponseDTO(Client client) {
        List<ClientAddressResponseDTO> addresses = client.getAddresses().stream()
                .map(this::mapToAddressResponseDTO)
                .collect(Collectors.toList());

        return ClientResponseDTO.builder()
                .id(client.getId())
                .tenantId(client.getTenantId())
                .nombreCompleto(client.getNombreCompleto())
                .telefono(client.getTelefono())
                .whatsapp(client.getWhatsapp())
                .email(client.getEmail())
                .esVip(client.getEsVip())
                .addresses(addresses)
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }

    private ClientAddressResponseDTO mapToAddressResponseDTO(ClientAddress address) {
        return ClientAddressResponseDTO.builder()
                .id(address.getId())
                .clientId(address.getClient().getId())
                .etiqueta(address.getEtiqueta())
                .direccion(address.getDireccion())
                .latitud(address.getLatitud())
                .longitud(address.getLongitud())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
