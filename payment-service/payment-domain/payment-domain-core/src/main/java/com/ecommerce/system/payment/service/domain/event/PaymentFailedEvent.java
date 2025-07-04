package com.ecommerce.system.payment.service.domain.event;

import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {

    public PaymentFailedEvent(Payment payment,
                              ZonedDateTime createdAt,
                              List<String> failureMessages) {
        super(payment, createdAt, failureMessages);
    }
}