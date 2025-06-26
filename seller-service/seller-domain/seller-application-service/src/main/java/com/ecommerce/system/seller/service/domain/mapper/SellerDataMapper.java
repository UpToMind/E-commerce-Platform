package com.ecommerce.system.seller.service.domain.mapper;

import com.ecommerce.system.domain.valueobject.Money;
import com.ecommerce.system.domain.valueobject.OrderId;
import com.ecommerce.system.domain.valueobject.OrderStatus;
import com.ecommerce.system.domain.valueobject.SellerId;
import com.ecommerce.system.seller.service.domain.dto.SellerApprovalRequest;
import com.ecommerce.system.seller.service.domain.entity.OrderDetail;
import com.ecommerce.system.seller.service.domain.entity.Product;
import com.ecommerce.system.seller.service.domain.entity.Seller;
import com.ecommerce.system.seller.service.domain.event.OrderApprovalEvent;
import com.ecommerce.system.seller.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SellerDataMapper {
    public Seller sellerApprovalRequestToSeller(SellerApprovalRequest
                                                                    sellerApprovalRequest) {
        return Seller.builder()
                .sellerId(new SellerId(UUID.fromString(sellerApprovalRequest.getSellerId())))
                .orderDetail(OrderDetail.builder()
                        .orderId(new OrderId(UUID.fromString(sellerApprovalRequest.getOrderId())))
                        .products(sellerApprovalRequest.getProducts().stream().map(
                                        product -> Product.builder()
                                                .productId(product.getId())
                                                .quantity(product.getQuantity())
                                                .build())
                                .collect(Collectors.toList()))
                        .totalAmount(new Money(sellerApprovalRequest.getPrice()))
                        .orderStatus(OrderStatus.valueOf(sellerApprovalRequest.getSellerOrderStatus().name()))
                        .build())
                .build();
    }

    public OrderEventPayload
    orderApprovalEventToOrderEventPayload(OrderApprovalEvent orderApprovalEvent) {
        return OrderEventPayload.builder()
                .orderId(orderApprovalEvent.getOrderApproval().getOrderId().getValue().toString())
                .sellerId(orderApprovalEvent.getSellerId().getValue().toString())
                .orderApprovalStatus(orderApprovalEvent.getOrderApproval().getApprovalStatus().name())
                .createdAt(orderApprovalEvent.getCreatedAt())
                .failureMessages(orderApprovalEvent.getFailureMessages())
                .build();
    }
}
