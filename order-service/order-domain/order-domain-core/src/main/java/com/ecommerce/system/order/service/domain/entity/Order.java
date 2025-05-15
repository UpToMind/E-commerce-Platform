package com.ecommerce.system.order.service.domain.entity;

import com.ecommerce.system.domain.entity.AggregateRoot;
import com.ecommerce.system.domain.valueobject.Money;
import com.ecommerce.system.domain.valueobject.OrderId;
import com.ecommerce.system.domain.valueobject.OrderStatus;
import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.order.service.domain.exception.OrderDomainException;
import com.ecommerce.system.order.service.domain.valueobject.OrderItemId;
import com.ecommerce.system.order.service.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {
    private final UserId userId;
    private final Money price;
    private final List<OrderItem> items;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failuerMessages;

    private Order(Builder builder) {
        super.setId(builder.orderId);
        userId = builder.userId;
        price = builder.price;
        items = builder.Items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failuerMessages = builder.failuerMessages;
    }

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    public void validateOrder(){
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    private void validateItemsPrice() {
        Money orderItemsTotal = items.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal)) {
            throw new OrderDomainException("Total price: " + price.getAmount()
                    + " is not equal to Order items total: " + orderItemsTotal.getAmount() + "!");
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("Order item price: " + orderItem.getPrice().getAmount() +
                    " is not valid for product " + orderItem.getProduct().getId().getValue());
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Total price must be greater than zero!");
        }
    }

    private void validateInitialOrder() {
        if (orderStatus != null || getId() != null) {
            throw new OrderDomainException("Order is not in correct state for initialization!");
        }
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem orderItem: items) {
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }


    public UserId getUserId() {
        return userId;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailuerMessages() {
        return failuerMessages;
    }

    public static final class Builder {
        private OrderId orderId;
        private UserId userId;
        private Money price;
        private List<OrderItem> Items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failuerMessages;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder userId(UserId val) {
            userId = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder Items(List<OrderItem> val) {
            Items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failuerMessages(List<String> val) {
            failuerMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
