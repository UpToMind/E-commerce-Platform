package com.ecommerce.system.payment.service.domain.ports.output.repository;

import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.payment.service.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {

    CreditHistory save(CreditHistory creditHistory);

    Optional<List<CreditHistory>> findByUserId(UserId userId);
}
