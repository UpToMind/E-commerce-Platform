package com.ecommerce.system.seller.service.domain.event;

import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.domain.valueobject.SellerId;
import com.ecommerce.system.seller.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent {

    public OrderRejectedEvent(OrderApproval orderApproval,
                              SellerId sellerId,
                              List<String> failureMessages,
                              ZonedDateTime createdAt) {
        super(orderApproval, sellerId, failureMessages, createdAt);
    }

}
