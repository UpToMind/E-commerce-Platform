package com.ecommerce.system.order.service.domain;

import com.ecommerce.system.order.service.domain.entity.Order;
import com.ecommerce.system.order.service.domain.entity.Product;
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

    private final String UTC = "UTC";

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Seller seller) {
        setOrderProductInformation(order, seller); 
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id: {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    private void setOrderProductInformation(Order order, Seller seller) {
        order.getItems().forEach(orderItem -> seller.getProducts().forEach(sellerProduct -> {
            Product currentProduct = orderItem.getProduct();
            if (currentProduct.equals(sellerProduct)) {
                currentProduct.updateWithConfirmedNameAndPrice(sellerProduct.getName(),
                        sellerProduct.getPrice());
            }
        }));
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id: {} is paid", order.getId().getValue());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        order.initCancel(failureMessages);
        log.info("Order with id: {} is cancelled", order.getId().getValue());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id: {} is cancelled", order.getId().getValue());
    }
}
