package com.ecommerce.system.order.service.domain.event;

import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent{
    public OrderPaidEvent(Order order,
                          ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}
