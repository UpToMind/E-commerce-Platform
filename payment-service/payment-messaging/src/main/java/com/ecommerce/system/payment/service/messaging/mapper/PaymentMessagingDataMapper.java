package com.ecommerce.system.payment.service.messaging.mapper;

import com.ecommerce.system.domain.valueobject.PaymentOrderStatus;
import com.ecommerce.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.ecommerce.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.ecommerce.system.kafka.order.avro.model.PaymentStatus;
import com.ecommerce.system.payment.service.domain.dto.PaymentRequest;
import com.ecommerce.system.payment.service.domain.event.PaymentCancelledEvent;
import com.ecommerce.system.payment.service.domain.event.PaymentCompletedEvent;
import com.ecommerce.system.payment.service.domain.event.PaymentFailedEvent;
import com.ecommerce.system.payment.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .userId(paymentRequestAvroModel.getUserId())
                .orderId(paymentRequestAvroModel.getOrderId())
                .price(paymentRequestAvroModel.getPrice())
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
                .build();
    }

    public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(String sagaId,
                                                                                OrderEventPayload orderEventPayload) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setPaymentId(orderEventPayload.getPaymentId())
                .setUserId(orderEventPayload.getUserId())
                .setOrderId(orderEventPayload.getOrderId())
                .setPrice(orderEventPayload.getPrice())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())//??
                .setPaymentStatus(PaymentStatus.valueOf(orderEventPayload.getPaymentStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }
}

