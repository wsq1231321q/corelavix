package com.laundry.core.repository;

import com.laundry.core.entity.RouteOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteOrderRepository extends JpaRepository<RouteOrder, Long> {
    List<RouteOrder> findByRouteId(Long routeId);
    List<RouteOrder> findByOrderId(Long orderId);
}
