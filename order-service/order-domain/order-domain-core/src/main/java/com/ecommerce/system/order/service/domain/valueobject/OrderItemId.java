package com.ecommerce.system.order.service.domain.valueobject;

import com.ecommerce.system.domain.valueobject.BaseId;

public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long value) {
        super(value);
    }
}
