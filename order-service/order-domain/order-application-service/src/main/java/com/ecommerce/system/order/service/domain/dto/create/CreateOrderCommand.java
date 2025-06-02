package com.ecommerce.system.order.service.domain.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderCommand {

    @NonNull
    private final UUID userId;
    @NonNull
    private final UUID sellerId;
    @NonNull
    private final BigDecimal price;
    @NonNull
    private final List<OrderItem> items;
}
