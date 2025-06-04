package com.ecommerce.system.order.service.domain.dto.message;

import com.ecommerce.system.domain.valueobject.OrderApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SellerApprovalResponse {
    private String id;
    private String sagaId;
    private String orderId;
    private String sellerId;
    private Instant createdAt;
    private OrderApprovalStatus orderApprovalStatus;
    private List<String> failureMessages;
}
