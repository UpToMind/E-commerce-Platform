package com.ecommerce.system.seller.service.domain.ports.output.repository;

import com.ecommerce.system.seller.service.domain.entity.Seller;

import java.util.Optional;

public interface SellerRepository {
    Optional<Seller> findSellerInformation(Seller seller);
}
