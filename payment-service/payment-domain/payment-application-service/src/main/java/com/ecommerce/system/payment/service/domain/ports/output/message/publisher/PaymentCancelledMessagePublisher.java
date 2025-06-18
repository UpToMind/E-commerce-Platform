package com.ecommerce.system.payment.service.domain.ports.output.message.publisher;

import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.payment.service.domain.event.PaymentCancelledEvent;

public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
