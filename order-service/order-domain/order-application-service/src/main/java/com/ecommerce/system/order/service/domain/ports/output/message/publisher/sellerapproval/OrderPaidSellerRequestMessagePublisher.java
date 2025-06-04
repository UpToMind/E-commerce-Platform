package com.ecommerce.system.order.service.domain.ports.output.message.publisher.sellerapproval;

import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidSellerRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
