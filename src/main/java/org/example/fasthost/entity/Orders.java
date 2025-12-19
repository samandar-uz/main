package org.example.fasthost.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.fasthost.entity.abs.BaseEntity;
import org.example.fasthost.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Orders extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariffs tariff;

    @Column(nullable = false)
    private Integer duration_days;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private String login;
    private String password;
    @Column
    private Integer cronJobsUsed;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (duration_days != null && endTime == null) {
            endTime = startTime.plusMonths(duration_days);
        }
    }
}
