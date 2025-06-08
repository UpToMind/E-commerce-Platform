package com.ecommerce.system.order.service.dataaccess.seller.mapper;

import com.ecommerce.system.domain.valueobject.Money;
import com.ecommerce.system.domain.valueobject.ProductId;
import com.ecommerce.system.domain.valueobject.SellerId;
import com.ecommerce.system.order.service.dataaccess.seller.entity.SellerEntity;
import com.ecommerce.system.order.service.dataaccess.seller.exception.SellerDataAccessException;
import com.ecommerce.system.order.service.domain.entity.Product;
import com.ecommerce.system.order.service.domain.entity.Seller;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SellerDataAccessMapper {
    public List<UUID> sellerToSellerProducts(Seller seller) {
        return seller.getProducts().stream()
                .map(product -> product.getId().getValue())
                .collect(Collectors.toList());
    }

    public Seller sellerEntityToSeller(List<SellerEntity> sellerEntities) {
        SellerEntity sellerEntity =
                sellerEntities.stream().findFirst().orElseThrow(() ->
                        new SellerDataAccessException("Seller could not be found!"));

        List<Product> sellerProducts = sellerEntities.stream().map(entity ->
                new Product(new ProductId(entity.getProductId()), entity.getProductName(),
                        new Money(entity.getProductPrice()))).toList();

        return Seller.builder()
                .sellerId(new SellerId(sellerEntity.getSellerId()))
                .products(sellerProducts)
                .build();
    }
}
