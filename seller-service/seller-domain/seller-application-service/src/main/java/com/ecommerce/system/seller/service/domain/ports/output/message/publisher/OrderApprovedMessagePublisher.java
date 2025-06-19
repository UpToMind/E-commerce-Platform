package com.ecommerce.system.seller.service.domain.ports.output.message.publisher;

import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.seller.service.domain.event.OrderApprovedEvent;

public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {
}
