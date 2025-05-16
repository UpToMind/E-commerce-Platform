package com.ecommerce.system.order.service.domain;

import com.ecommerce.system.order.service.domain.entity.Order;
import com.ecommerce.system.order.service.domain.entity.Seller;
import com.ecommerce.system.order.service.domain.event.OrderCancelledEvent;
import com.ecommerce.system.order.service.domain.event.OrderCreatedEvent;
import com.ecommerce.system.order.service.domain.event.OrderPaidEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService{

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Seller seller) {
        setOrderProductInformation(order, seller);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id: {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    private void setOrderProductInformation(Order order, Seller seller) {
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        return null;
    }

    @Override
    public void approveOrder(Order order) {

    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        return null;
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {

    }
}
