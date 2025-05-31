package com.ecommerce.system.order.service.domain.ports.output.repository;

import com.ecommerce.system.domain.valueobject.ProductId;
import com.ecommerce.system.order.service.domain.entity.Seller;

import java.util.Optional;

public interface SellerRepository {
    Optional<Seller> findSellerInformation(Seller seller);
}
