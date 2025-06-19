package com.ecommerce.system.seller.service.domain.entity;

import com.ecommerce.system.domain.entity.BaseEntity;
import com.ecommerce.system.domain.valueobject.OrderApprovalStatus;
import com.ecommerce.system.domain.valueobject.OrderId;
import com.ecommerce.system.domain.valueobject.SellerId;
import com.ecommerce.system.seller.service.domain.valueobject.OrderApprovalId;

public class OrderApproval extends BaseEntity<OrderApprovalId> {
    private final SellerId sellerId;
    private final OrderId orderId;
    private final OrderApprovalStatus approvalStatus;

    private OrderApproval(Builder builder) {
        setId(builder.orderApprovalId);
        sellerId = builder.sellerId;
        orderId = builder.orderId;
        approvalStatus = builder.approvalStatus;
    }

    public static Builder builder() {
        return new Builder();
    }


    public SellerId getSellerId() {
        return sellerId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public static final class Builder {
        private OrderApprovalId orderApprovalId;
        private SellerId sellerId;
        private OrderId orderId;
        private OrderApprovalStatus approvalStatus;

        private Builder() {
        }

        public Builder orderApprovalId(OrderApprovalId val) {
            orderApprovalId = val;
            return this;
        }

        public Builder sellerId(SellerId val) {
            sellerId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder approvalStatus(OrderApprovalStatus val) {
            approvalStatus = val;
            return this;
        }

        public OrderApproval build() {
            return new OrderApproval(this);
        }
    }
}

