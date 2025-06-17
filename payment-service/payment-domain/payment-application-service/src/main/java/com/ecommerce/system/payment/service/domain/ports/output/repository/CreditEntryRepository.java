package com.ecommerce.system.payment.service.domain.ports.output.repository;

import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.payment.service.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByUserId(UserId userId);
}
