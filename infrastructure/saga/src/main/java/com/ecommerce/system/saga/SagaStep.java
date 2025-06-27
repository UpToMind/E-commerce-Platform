package com.ecommerce.system.saga;

import com.ecommerce.system.domain.event.DomainEvent;

public interface SagaStep<T> {
    void process(T data);
    void rollback(T data);
}
