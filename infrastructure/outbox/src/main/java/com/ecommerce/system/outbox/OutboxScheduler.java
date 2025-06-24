package com.ecommerce.system.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
