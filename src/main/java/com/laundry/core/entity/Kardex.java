package com.laundry.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "kardex")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kardex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private KardexType tipo;

    @Column(name = "costo", precision = 10, scale = 2, nullable = false)
    private BigDecimal costo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum KardexType {
        DETERGENTE, AGUA, ELECTRICIDAD, REPROCESO
    }
}
