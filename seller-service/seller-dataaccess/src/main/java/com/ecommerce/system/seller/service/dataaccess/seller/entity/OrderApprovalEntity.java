package com.ecommerce.system.seller.service.dataaccess.seller.entity;

import com.ecommerce.system.domain.valueobject.OrderApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_approval", schema = "seller")
@Entity
public class OrderApprovalEntity {

    @Id
    private UUID id;
    private UUID sellerId;
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    private OrderApprovalStatus status;
}
