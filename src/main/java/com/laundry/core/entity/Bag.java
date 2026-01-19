package com.laundry.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "qr_code", unique = true, nullable = false)
    private String qrCode;

    @Column(name = "disponible")
    private Boolean disponible = true;

    @OneToMany(mappedBy = "bag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BagAssignment> assignments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
