package com.ecommerce.system.order.service.domain.ports.output.message.publisher.payment;

import com.ecommerce.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.ecommerce.system.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface PaymentRequestMessagePublisher {

    void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                 BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback);
}

