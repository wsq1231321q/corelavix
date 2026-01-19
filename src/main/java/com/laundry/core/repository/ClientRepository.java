package com.laundry.core.repository;

import com.laundry.core.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByTenantId(Long tenantId);
    Optional<Client> findByIdAndTenantId(Long id, Long tenantId);
    boolean existsByIdAndTenantId(Long id, Long tenantId);
}
