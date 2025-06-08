package com.ecommerce.system.order.service.dataaccess.seller.repository;

import com.ecommerce.system.order.service.dataaccess.seller.entity.SellerEntity;
import com.ecommerce.system.order.service.dataaccess.seller.entity.SellerEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerJpaRepository extends JpaRepository<SellerEntity, SellerEntityId> {
    Optional<List<SellerEntity>> findBySellerIdAndProductIdIn(UUID sellerId, List<UUID> productIds);
}
