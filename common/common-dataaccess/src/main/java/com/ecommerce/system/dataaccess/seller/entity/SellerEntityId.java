package com.ecommerce.system.dataaccess.seller.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerEntityId implements Serializable {
    private UUID sellerId;
    private UUID productId;

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SellerEntityId that = (SellerEntityId) o;
        return Objects.equals(sellerId, that.sellerId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellerId, productId);
    }
}
