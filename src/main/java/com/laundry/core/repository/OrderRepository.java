package com.laundry.core.repository;

import com.laundry.core.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTenantId(Long tenantId);
    Optional<Order> findByIdAndTenantId(Long id, Long tenantId);
    List<Order> findByClientId(Long clientId);
}
