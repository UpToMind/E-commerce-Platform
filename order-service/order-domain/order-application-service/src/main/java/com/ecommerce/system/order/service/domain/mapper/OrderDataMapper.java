package com.ecommerce.system.order.service.domain.mapper;

import com.ecommerce.system.domain.valueobject.Money;
import com.ecommerce.system.domain.valueobject.ProductId;
import com.ecommerce.system.domain.valueobject.SellerId;
import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ecommerce.system.order.service.domain.dto.create.CreateOrderResponse;
import com.ecommerce.system.order.service.domain.dto.track.TrackOrderResponse;
import com.ecommerce.system.order.service.domain.entity.Order;
import com.ecommerce.system.order.service.domain.entity.OrderItem;
import com.ecommerce.system.order.service.domain.entity.Product;
import com.ecommerce.system.order.service.domain.entity.Seller;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {
    public Seller createOrderCommandToSeller(CreateOrderCommand createOrderCommand) {
        return Seller.builder()
                .sellerId(new SellerId(createOrderCommand.getSellerId()))
                .products(createOrderCommand.getItems().stream().map(orderItem ->
                                new Product(new ProductId(orderItem.getProductId())))
                        .collect(Collectors.toList()))
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .userId(new UserId(createOrderCommand.getUserId()))
                .sellerId(new SellerId(createOrderCommand.getSellerId()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntities(
            List<com.ecommerce.system.order.service.domain.dto.create.OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem ->
                        OrderItem.builder()
                                .product(new Product(new ProductId(orderItem.getProductId())))
                                .price(new Money(orderItem.getPrice()))
                                .quantity(orderItem.getQuantity())
                                .subTotal(new Money(orderItem.getSubTotal()))
                                .build()).collect(Collectors.toList());
    }

}
