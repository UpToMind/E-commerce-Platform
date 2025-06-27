package com.ecommerce.system.seller.service.domain.ports.output.message.publisher;

import com.ecommerce.system.outbox.OutboxStatus;
import com.ecommerce.system.seller.service.domain.outbox.model.OrderOutboxMessage;


import java.util.function.BiConsumer;

public interface SellerApprovalResponseMessagePublisher {
    void publish(OrderOutboxMessage orderOutboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
