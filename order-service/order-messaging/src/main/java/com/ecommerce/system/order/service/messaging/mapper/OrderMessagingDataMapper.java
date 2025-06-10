package com.ecommerce.system.order.service.messaging.mapper;

import com.ecommerce.system.kafka.order.avro.model.PaymentOrderStatus;
import com.ecommerce.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.ecommerce.system.kafka.order.avro.model.SellerApprovalRequestAvroModel;
import com.ecommerce.system.kafka.order.avro.model.sellerOrderStatus;
import com.ecommerce.system.order.service.domain.entity.Order;
import com.ecommerce.system.order.service.domain.event.OrderCancelledEvent;
import com.ecommerce.system.order.service.domain.event.OrderCreatedEvent;
import com.ecommerce.system.order.service.domain.event.OrderPaidEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {
    public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setUserId(order.getUserId().getValue().toString())
                .setOrderId(order.getId().getValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {
        Order order = orderCancelledEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setUserId(order.getUserId().getValue().toString())
                .setOrderId(order.getId().getValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();
    }

    public SellerApprovalRequestAvroModel
    orderPaidEventToSellerApprovalRequestAvroModel(OrderPaidEvent orderPaidEvent) {
        Order order = orderPaidEvent.getOrder();
        return SellerApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setOrderId(order.getId().getValue().toString())
                .setSellerId(order.getSellerId().getValue().toString())
                .setOrderId(order.getId().getValue().toString())
                .setSellerOrderStatus(com.ecommerce.system.kafka.order.avro.model.sellerOrderStatus
                        .valueOf(order.getOrderStatus().name()))
                .setProducts(order.getItems().stream().map(orderItem ->
                        com.ecommerce.system.kafka.order.avro.model.Product.newBuilder()
                                .setId(orderItem.getProduct().getId().getValue().toString())
                                .setQuantity(orderItem.getQuantity())
                                .build()).collect(Collectors.toList()))
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
                .setSellerOrderStatus(sellerOrderStatus.PAID)
                .build();
    }

}
