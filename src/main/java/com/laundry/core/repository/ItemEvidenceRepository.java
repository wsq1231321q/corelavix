package com.laundry.core.repository;

import com.laundry.core.entity.ItemEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemEvidenceRepository extends JpaRepository<ItemEvidence, Long> {
    List<ItemEvidence> findByOrderItemId(Long orderItemId);
}
