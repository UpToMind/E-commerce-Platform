package com.ecommerce.system.order.service.domain.ports.output.message.publisher.sellerapproval;

import com.ecommerce.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.ecommerce.system.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface SellerApprovalRequestMessagePublisher {
    void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                 BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback);
}
