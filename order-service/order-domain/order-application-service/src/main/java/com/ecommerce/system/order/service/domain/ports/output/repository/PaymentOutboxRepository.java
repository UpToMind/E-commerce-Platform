package com.ecommerce.system.order.service.domain.ports.output.repository;

import com.ecommerce.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.ecommerce.system.outbox.OutboxStatus;
import com.ecommerce.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentOutboxRepository {

    OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage);

    Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                     OutboxStatus outboxStatus,
                                                                                     SagaStatus... sagaStatus);
    Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String type,
                                                                         UUID sagaId,
                                                                         SagaStatus... sagaStatus);
    void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                  OutboxStatus outboxStatus,
                                                  SagaStatus... sagaStatus);
}
