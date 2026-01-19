package com.laundry.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "color")
    private String color;

    @Column(name = "material")
    private String material;

    @Column(name = "es_delicado")
    private Boolean esDelicado = false;

    @Column(name = "peso_kg", precision = 8, scale = 3)
    private BigDecimal pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private ItemStatus estado = ItemStatus.RECIBIDO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bag_id")
    private Bag bag;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEvidence> evidences = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ItemStatus {
        RECIBIDO, LAVANDO, LISTO, EN_RUTA, ENTREGADO
    }
}
