package com.laundry.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEvidence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private EvidenceType tipo;

    @Column(name = "etiqueta")
    private String etiqueta;

    @Lob
    private String fotoBase64;

    @Column(name = "geo_lat")
    private Double geoLat;

    @Column(name = "geo_lng")
    private Double geoLng;

    @Column(name = "creado_por")
    private Long creadoPor;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum EvidenceType {
        DAÑO, RECOJO, ENTREGA, REPROCESO
    }
}
