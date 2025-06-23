package com.ecommerce.system.seller.service.domain.entity;

import com.ecommerce.system.domain.entity.AggregateRoot;
import com.ecommerce.system.domain.valueobject.Money;
import com.ecommerce.system.domain.valueobject.OrderApprovalStatus;
import com.ecommerce.system.domain.valueobject.OrderStatus;
import com.ecommerce.system.domain.valueobject.SellerId;
import com.ecommerce.system.seller.service.domain.valueobject.OrderApprovalId;

import java.util.List;
import java.util.UUID;

public class Seller extends AggregateRoot<SellerId> {
    private OrderApproval orderApproval;
    private final OrderDetail orderDetail;

    public void validateOrder(List<String> failureMessages) {
        if (orderDetail.getOrderStatus() != OrderStatus.PAID) {
            failureMessages.add("Payment is not completed for order: " + orderDetail.getId());
        }
        Money totalAmount = orderDetail.getProducts().stream().map(product -> {
            if (!product.isAvailable()) {
                failureMessages.add("Product with id: " + product.getId().getValue()
                        + " is not available");
            }
            return product.getPrice().multiply(product.getQuantity());
        }).reduce(Money.ZERO, Money::add);

        if (!totalAmount.equals(orderDetail.getTotalAmount())) {
            failureMessages.add("Price total is not correct for order: " + orderDetail.getId());
        }
    }

    public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
        this.orderApproval = OrderApproval.builder()
                .orderApprovalId(new OrderApprovalId(UUID.randomUUID()))
                .sellerId(this.getId())
                .orderId(this.getOrderDetail().getId())
                .approvalStatus(orderApprovalStatus)
                .build();
    }

    private Seller(Builder builder) {
        setId(builder.sellerId);
        orderApproval = builder.orderApproval;
        orderDetail = builder.orderDetail;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public static final class Builder {
        private SellerId sellerId;
        private OrderApproval orderApproval;
        private OrderDetail orderDetail;

        private Builder() {
        }

        public Builder sellerId(SellerId val) {
            sellerId = val;
            return this;
        }

        public Builder orderApproval(OrderApproval val) {
            orderApproval = val;
            return this;
        }

        public Builder orderDetail(OrderDetail val) {
            orderDetail = val;
            return this;
        }

        public Seller build() {
            return new Seller(this);
        }
    }
}
