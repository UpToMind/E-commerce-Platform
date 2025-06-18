package com.ecommerce.system.payment.service.dataacess.credithistory.repository;

import com.ecommerce.system.payment.service.dataacess.credithistory.entity.CreditHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditHistoryJpaRepository extends JpaRepository<CreditHistoryEntity, UUID> {

    Optional<List<CreditHistoryEntity>> findByUserId(UUID userId);
}
