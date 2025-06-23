package com.ecommerce.system.seller.service.domain.dto;

import com.ecommerce.system.domain.valueobject.SellerOrderStatus;
import com.ecommerce.system.seller.service.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SellerApprovalRequest {
    private String id;
    private String sagaId;
    private String sellerId;
    private String orderId;
    private SellerOrderStatus sellerOrderStatus;
    private java.util.List<Product> products;
    private java.math.BigDecimal price;
    private java.time.Instant createdAt;
}
