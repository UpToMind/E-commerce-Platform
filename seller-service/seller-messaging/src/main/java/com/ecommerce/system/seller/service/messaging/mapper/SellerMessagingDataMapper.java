package com.ecommerce.system.seller.service.messaging.mapper;

import com.ecommerce.system.domain.valueobject.ProductId;
import com.ecommerce.system.domain.valueobject.SellerOrderStatus;
import com.ecommerce.system.kafka.order.avro.model.OrderApprovalStatus;
import com.ecommerce.system.kafka.order.avro.model.SellerApprovalRequestAvroModel;
import com.ecommerce.system.kafka.order.avro.model.SellerApprovalResponseAvroModel;
import com.ecommerce.system.seller.service.domain.dto.SellerApprovalRequest;
import com.ecommerce.system.seller.service.domain.entity.Product;
import com.ecommerce.system.seller.service.domain.event.OrderApprovedEvent;
import com.ecommerce.system.seller.service.domain.event.OrderRejectedEvent;
import com.ecommerce.system.seller.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SellerMessagingDataMapper {

    public SellerApprovalRequest
    sellerApprovalRequestAvroModelToSellerApproval(SellerApprovalRequestAvroModel
                                                                   sellerApprovalRequestAvroModel) {
        return SellerApprovalRequest.builder()
                .id(sellerApprovalRequestAvroModel.getId())
                .sagaId(sellerApprovalRequestAvroModel.getSagaId())
                .sellerId(sellerApprovalRequestAvroModel.getSellerId())
                .orderId(sellerApprovalRequestAvroModel.getOrderId())
                .sellerOrderStatus(SellerOrderStatus.valueOf(sellerApprovalRequestAvroModel
                        .getSellerOrderStatus().name()))
                .products(sellerApprovalRequestAvroModel.getProducts()
                        .stream().map(avroModel ->
                                Product.builder()
                                        .productId(new ProductId(UUID.fromString(avroModel.getId())))
                                        .quantity(avroModel.getQuantity())
                                        .build())
                        .collect(Collectors.toList()))
                .price(sellerApprovalRequestAvroModel.getPrice())
                .createdAt(sellerApprovalRequestAvroModel.getCreatedAt())
                .build();
    }

    public SellerApprovalResponseAvroModel
    orderEventPayloadToSellerApprovalResponseAvroModel(String sagaId, OrderEventPayload orderEventPayload) {
        return SellerApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderEventPayload.getOrderId())
                .setSellerId(orderEventPayload.getSellerId())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
                .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderEventPayload.getOrderApprovalStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }
}
