package com.ecommerce.system.domain.event.publisher;

import com.ecommerce.system.domain.event.DomainEvent;

public interface DomainEventPublisher <T extends DomainEvent>{
    void publish(T domainEvent);
}
