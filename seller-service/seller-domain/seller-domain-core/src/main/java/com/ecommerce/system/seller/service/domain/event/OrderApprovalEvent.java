package com.ecommerce.system.seller.service.domain.event;

import com.ecommerce.system.domain.event.DomainEvent;
import com.ecommerce.system.domain.valueobject.SellerId;
import com.ecommerce.system.seller.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {
    private final OrderApproval orderApproval;
    private final SellerId sellerId;
    private final List<String> failureMessages;
    private final ZonedDateTime createdAt;

    public OrderApprovalEvent(OrderApproval orderApproval,
                              SellerId sellerId,
                              List<String> failureMessages,
                              ZonedDateTime createdAt) {
        this.orderApproval = orderApproval;
        this.sellerId = sellerId;
        this.failureMessages = failureMessages;
        this.createdAt = createdAt;
    }

    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public SellerId getSellerId() {
        return sellerId;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
