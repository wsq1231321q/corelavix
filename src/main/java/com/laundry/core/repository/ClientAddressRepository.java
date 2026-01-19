package com.laundry.core.repository;

import com.laundry.core.entity.ClientAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientAddressRepository extends JpaRepository<ClientAddress, Long> {
    List<ClientAddress> findByClientId(Long clientId);
    Optional<ClientAddress> findByIdAndClientId(Long id, Long clientId);
}
