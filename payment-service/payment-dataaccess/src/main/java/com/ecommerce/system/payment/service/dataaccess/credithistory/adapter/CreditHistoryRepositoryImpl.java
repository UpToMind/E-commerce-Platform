package com.ecommerce.system.payment.service.dataaccess.credithistory.adapter;

import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.ecommerce.system.payment.service.dataaccess.credithistory.mapper.CreditHistoryDataAccessMapper;
import com.ecommerce.system.payment.service.dataaccess.credithistory.repository.CreditHistoryJpaRepository;
import com.ecommerce.system.payment.service.domain.entity.CreditHistory;
import com.ecommerce.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

    public CreditHistoryRepositoryImpl(CreditHistoryJpaRepository creditHistoryJpaRepository,
                                       CreditHistoryDataAccessMapper creditHistoryDataAccessMapper) {
        this.creditHistoryJpaRepository = creditHistoryJpaRepository;
        this.creditHistoryDataAccessMapper = creditHistoryDataAccessMapper;
    }

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        return creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(creditHistoryJpaRepository
                .save(creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)));
    }

    @Override
    public Optional<List<CreditHistory>> findByUserId(UserId userId) {
        Optional<List<CreditHistoryEntity>> creditHistory =
                creditHistoryJpaRepository.findByUserId(userId.getValue());
        return creditHistory
                .map(creditHistoryList ->
                        creditHistoryList.stream()
                                .map(creditHistoryDataAccessMapper::creditHistoryEntityToCreditHistory)
                                .collect(Collectors.toList()));
    }
}
