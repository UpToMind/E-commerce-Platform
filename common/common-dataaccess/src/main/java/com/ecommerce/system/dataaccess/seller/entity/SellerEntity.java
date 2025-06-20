package com.ecommerce.system.dataaccess.seller.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(SellerEntityId.class)
@Table(name = "order_seller_m_view", schema = "seller")
@Entity
public class SellerEntity {

    @Id
    private UUID sellerId;
    @Id
    private UUID productId;
    private String sellerName;
    private String productName;
    private BigDecimal productPrice;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SellerEntity that = (SellerEntity) o;
        return Objects.equals(sellerId, that.sellerId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellerId, productId);
    }
}
