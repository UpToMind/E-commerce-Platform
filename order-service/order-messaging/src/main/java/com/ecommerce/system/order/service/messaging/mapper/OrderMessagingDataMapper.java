package com.ecommerce.system.order.service.messaging.mapper;

import com.ecommerce.system.kafka.order.avro.model.*;
import com.ecommerce.system.order.service.domain.dto.message.PaymentResponse;
import com.ecommerce.system.order.service.domain.dto.message.SellerApprovalResponse;
import com.ecommerce.system.order.service.domain.entity.Order;
import com.ecommerce.system.order.service.domain.event.OrderCancelledEvent;
import com.ecommerce.system.order.service.domain.event.OrderCreatedEvent;
import com.ecommerce.system.order.service.domain.event.OrderPaidEvent;
import com.ecommerce.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.ecommerce.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {
    public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentResponseAvroModel
                                                                             paymentResponseAvroModel) {
        return PaymentResponse.builder()
                .id(paymentResponseAvroModel.getId())
                .sagaId(paymentResponseAvroModel.getSagaId())
                .paymentId(paymentResponseAvroModel.getPaymentId())
                .userId(paymentResponseAvroModel.getUserId())
                .orderId(paymentResponseAvroModel.getOrderId())
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
                .id(sellerApprovalResponseAvroModel.getId())
                .sagaId(sellerApprovalResponseAvroModel.getSagaId())
                .sellerId(sellerApprovalResponseAvroModel.getSellerId())
                .orderId(sellerApprovalResponseAvroModel.getOrderId())
                .createdAt(sellerApprovalResponseAvroModel.getCreatedAt())
                .orderApprovalStatus(com.ecommerce.system.domain.valueobject.OrderApprovalStatus.valueOf(
                        sellerApprovalResponseAvroModel.getOrderApprovalStatus().name()))
                .failureMessages(sellerApprovalResponseAvroModel.getFailureMessages())
                .build();
    }

    public PaymentRequestAvroModel orderPaymentEventToPaymentRequestAvroModel(String sagaId, OrderPaymentEventPayload
            orderPaymentEventPayload) {
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setUserId(orderPaymentEventPayload.getUserId())
                .setOrderId(orderPaymentEventPayload.getOrderId())
                .setPrice(orderPaymentEventPayload.getPrice())
                .setCreatedAt(orderPaymentEventPayload.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
                .build();
    }

    public SellerApprovalRequestAvroModel
    orderApprovalEventToSellerApprovalRequestAvroModel(String sagaId, OrderApprovalEventPayload
            orderApprovalEventPayload) {
        return SellerApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderApprovalEventPayload.getOrderId())
                .setSellerId(orderApprovalEventPayload.getSellerId())
                .setSellerOrderStatus(SellerOrderStatus
                        .valueOf(orderApprovalEventPayload.getSellerOrderStatus()))
                .setProducts(orderApprovalEventPayload.getProducts().stream().map(orderApprovalEventProduct ->
                        com.ecommerce.system.kafka.order.avro.model.Product.newBuilder()
                                .setId(orderApprovalEventProduct.getId())
                                .setQuantity(orderApprovalEventProduct.getQuantity())
                                .build()).collect(Collectors.toList()))
                .setPrice(orderApprovalEventPayload.getPrice())
                .setCreatedAt(orderApprovalEventPayload.getCreatedAt().toInstant())
                .build();
    }

}
