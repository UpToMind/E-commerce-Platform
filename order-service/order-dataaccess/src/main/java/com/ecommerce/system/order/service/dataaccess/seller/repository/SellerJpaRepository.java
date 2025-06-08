package com.ecommerce.system.order.service.dataaccess.seller.repository;

import com.ecommerce.system.order.service.dataaccess.seller.entity.SellerEntity;
import com.ecommerce.system.order.service.dataaccess.seller.entity.SellerEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SellerJpaRepository extends JpaRepository<SellerEntity, SellerEntityId> {
    Optional<List<SellerEntity>> findBySellerIdAndProductIdIn(UUID sellerId, List<UUID> productIds);
}
