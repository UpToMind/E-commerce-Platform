package com.ecommerce.system.domain.event;

public interface DomainEvent<T> {
    void fire();
}
