package com.ecommerce.system.seller.service.dataaccess.seller.repository;

import com.ecommerce.system.seller.service.dataaccess.seller.entity.OrderApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderApprovalJpaRepository extends JpaRepository<OrderApprovalEntity, UUID> {
}
