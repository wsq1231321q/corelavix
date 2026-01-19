package com.laundry.core.repository;

import com.laundry.core.entity.BagAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BagAssignmentRepository extends JpaRepository<BagAssignment, Long> {
    List<BagAssignment> findByBagId(Long bagId);
    List<BagAssignment> findByOrderId(Long orderId);
    Optional<BagAssignment> findByBagIdAndLiberadoEnIsNull(Long bagId);
}
