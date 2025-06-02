package com.ecommerce.system.order.service.domain.ports.output.message.publisher.payment;

import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.order.service.domain.event.OrderCancelledEvent;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
