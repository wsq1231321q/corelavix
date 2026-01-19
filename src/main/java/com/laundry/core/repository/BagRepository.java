package com.laundry.core.repository;

import com.laundry.core.entity.Bag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BagRepository extends JpaRepository<Bag, Long> {
    List<Bag> findByTenantId(Long tenantId);
    Optional<Bag> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Bag> findByQrCode(String qrCode);
    boolean existsByQrCode(String qrCode);
}
