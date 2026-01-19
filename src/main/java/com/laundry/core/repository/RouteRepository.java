package com.laundry.core.repository;

import com.laundry.core.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByTenantId(Long tenantId);
    Optional<Route> findByIdAndTenantId(Long id, Long tenantId);
    List<Route> findByDriverId(Long driverId);
}
