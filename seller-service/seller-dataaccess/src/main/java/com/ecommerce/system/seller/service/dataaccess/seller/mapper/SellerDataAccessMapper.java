package com.ecommerce.system.seller.service.dataaccess.seller.mapper;

import com.ecommerce.system.dataaccess.seller.entity.SellerEntity;
import com.ecommerce.system.dataaccess.seller.exception.SellerDataAccessException;
import com.ecommerce.system.domain.valueobject.Money;
import com.ecommerce.system.domain.valueobject.OrderId;
import com.ecommerce.system.domain.valueobject.ProductId;
import com.ecommerce.system.domain.valueobject.SellerId;
import com.ecommerce.system.seller.service.dataaccess.seller.entity.OrderApprovalEntity;
import com.ecommerce.system.seller.service.domain.entity.OrderApproval;
import com.ecommerce.system.seller.service.domain.entity.OrderDetail;
import com.ecommerce.system.seller.service.domain.entity.Product;
import com.ecommerce.system.seller.service.domain.entity.Seller;
import com.ecommerce.system.seller.service.domain.valueobject.OrderApprovalId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SellerDataAccessMapper {
    public List<UUID> sellerToSellerProducts(Seller seller) {
        return seller.getOrderDetail().getProducts().stream()
                .map(product -> product.getId().getValue())
                .collect(Collectors.toList());
    }

    public Seller sellerEntityToSeller(List<SellerEntity> sellerEntities) {
        SellerEntity sellerEntity =
                sellerEntities.stream().findFirst().orElseThrow(() ->
                        new SellerDataAccessException("No sellers found!"));

        List<Product> sellerProducts = sellerEntities.stream().map(entity ->
                        Product.builder()
                                .productId(new ProductId(entity.getProductId()))
                                .name(entity.getProductName())
                                .price(new Money(entity.getProductPrice()))
                                .build())
                .collect(Collectors.toList());

        return Seller.builder()
                .sellerId(new SellerId(sellerEntity.getSellerId()))
                .orderDetail(OrderDetail.builder()
                        .products(sellerProducts)
                        .build())
                .build();
    }

    public OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval orderApproval) {
        return OrderApprovalEntity.builder()
                .id(orderApproval.getId().getValue())
                .sellerId(orderApproval.getSellerId().getValue())
                .orderId(orderApproval.getOrderId().getValue())
                .status(orderApproval.getApprovalStatus())
                .build();
    }

    public OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity orderApprovalEntity) {
        return OrderApproval.builder()
                .orderApprovalId(new OrderApprovalId(orderApprovalEntity.getId()))
                .sellerId(new SellerId(orderApprovalEntity.getSellerId()))
                .orderId(new OrderId(orderApprovalEntity.getOrderId()))
                .approvalStatus(orderApprovalEntity.getStatus())
                .build();
    }
}
