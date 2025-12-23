package org.example.fasthost.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.fasthost.entity.abs.BaseEntity;
import org.example.fasthost.entity.enums.PaymentMethod;
import org.example.fasthost.entity.enums.PaymentStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "payments", indexes = {
        @Index(columnList = "paymentId", unique = true),
        @Index(columnList = "user_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

}
