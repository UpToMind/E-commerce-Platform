package com.ecommerce.system.order.service.dataaccess.seller.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_seller_m_view", schema = "seller")
@Entity
public class SellerEntity {

    @Id
    private UUID sellerId;
    @Id
    private UUID productId;
}
