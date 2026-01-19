package com.laundry.core.repository;

import com.laundry.core.entity.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<Kardex, Long> {
    List<Kardex> findByOrderId(Long orderId);
}
