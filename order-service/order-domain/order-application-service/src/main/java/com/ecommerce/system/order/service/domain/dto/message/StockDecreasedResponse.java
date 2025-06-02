package com.ecommerce.system.order.service.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StockDecreasedResponse {
    private String orderId;
    private String productId;
    private int quantity;
    private String status;
    private String message;
}
