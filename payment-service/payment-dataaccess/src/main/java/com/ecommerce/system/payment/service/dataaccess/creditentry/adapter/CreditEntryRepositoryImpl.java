package com.ecommerce.system.payment.service.dataaccess.creditentry.adapter;

import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.payment.service.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper;
import com.ecommerce.system.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import com.ecommerce.system.payment.service.domain.entity.CreditEntry;
import com.ecommerce.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository creditEntryJpaRepository,
                                     CreditEntryDataAccessMapper creditEntryDataAccessMapper) {
        this.creditEntryJpaRepository = creditEntryJpaRepository;
        this.creditEntryDataAccessMapper = creditEntryDataAccessMapper;
    }

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        return creditEntryDataAccessMapper
                .creditEntryEntityToCreditEntry(creditEntryJpaRepository
                        .save(creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)));
    }

    @Override
    public Optional<CreditEntry> findByUserId(UserId userId) {
        return creditEntryJpaRepository
                .findByUserId(userId.getValue())
                .map(creditEntryDataAccessMapper::creditEntryEntityToCreditEntry);
    }
}
