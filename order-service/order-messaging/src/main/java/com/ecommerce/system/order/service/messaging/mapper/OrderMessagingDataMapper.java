package com.ecommerce.system.order.service.messaging.mapper;

import com.ecommerce.system.kafka.order.avro.model.*;
import com.ecommerce.system.order.service.domain.dto.message.PaymentResponse;
import com.ecommerce.system.order.service.domain.dto.message.SellerApprovalResponse;
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
                .setId(UUID.randomUUID())
                .setSagaId(null)
                .setUserId(order.getUserId().getValue())
                .setOrderId(order.getId().getValue())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {
        Order order = orderCancelledEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(null)
                .setUserId(order.getUserId().getValue())
                .setOrderId(order.getId().getValue())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();
    }

    public SellerApprovalRequestAvroModel
    orderPaidEventToSellerApprovalRequestAvroModel(OrderPaidEvent orderPaidEvent) {
        Order order = orderPaidEvent.getOrder();
        return SellerApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(null)
                .setOrderId(order.getId().getValue())
                .setSellerId(order.getSellerId().getValue())
                .setSellerOrderStatus(com.ecommerce.system.kafka.order.avro.model.sellerOrderStatus.PAID)
                .setProducts(order.getItems().stream().map(orderItem ->
                        com.ecommerce.system.kafka.order.avro.model.Product.newBuilder()
                                .setId(orderItem.getProduct().getId().getValue().toString())
                                .setQuantity(orderItem.getQuantity())
                                .build()).collect(Collectors.toList()))
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
                .build();
    }

    public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentResponseAvroModel
                                                                             paymentResponseAvroModel) {
        return PaymentResponse.builder()
                .id(paymentResponseAvroModel.getId().toString())
                .sagaId(paymentResponseAvroModel.getSagaId().toString())
                .paymentId(paymentResponseAvroModel.getPaymentId().toString())
                .userId(paymentResponseAvroModel.getUserId().toString())
                .orderId(paymentResponseAvroModel.getOrderId().toString())
                .price(paymentResponseAvroModel.getPrice())
                .createdAt(paymentResponseAvroModel.getCreatedAt())
                .paymentStatus(com.ecommerce.system.domain.valueobject.PaymentStatus.valueOf(
                        paymentResponseAvroModel.getPaymentStatus().name()))
                .failureMessages(paymentResponseAvroModel.getFailureMessages())
                .build();
    }

    public SellerApprovalResponse
    approvalResponseAvroModelToApprovalResponse(SellerApprovalResponseAvroModel
                                                        sellerApprovalResponseAvroModel) {
        return SellerApprovalResponse.builder()
                .id(sellerApprovalResponseAvroModel.getId().toString())
                .sagaId(sellerApprovalResponseAvroModel.getSagaId().toString())
                .sellerId(sellerApprovalResponseAvroModel.getSellerId().toString())
                .orderId(sellerApprovalResponseAvroModel.getOrderId().toString())
                .createdAt(sellerApprovalResponseAvroModel.getCreatedAt())
                .orderApprovalStatus(com.ecommerce.system.domain.valueobject.OrderApprovalStatus.valueOf(
                        sellerApprovalResponseAvroModel.getOrderApprovalStatus().name()))
                .failureMessages(sellerApprovalResponseAvroModel.getFailureMessages())
                .build();
    }

}
