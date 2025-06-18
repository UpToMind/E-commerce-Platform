package com.ecommerce.system.payment.service.dataacess.credithistory.mapper;

import com.ecommerce.system.domain.valueobject.Money;
import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.payment.service.dataacess.credithistory.entity.CreditHistoryEntity;
import com.ecommerce.system.payment.service.domain.entity.CreditHistory;
import com.ecommerce.system.payment.service.domain.valueobject.CreditHistoryId;
import org.springframework.stereotype.Component;

@Component
public class CreditHistoryDataAccessMapper {

    public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
        return CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
                .userId(new UserId(creditHistoryEntity.getUserId()))
                .amount(new Money(creditHistoryEntity.getAmount()))
                .transactionType(creditHistoryEntity.getType())
                .build();
    }

    public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
        return CreditHistoryEntity.builder()
                .id(creditHistory.getId().getValue())
                .userId(creditHistory.getUserId().getValue())
                .amount(creditHistory.getAmount().getAmount())
                .type(creditHistory.getTransactionType())
                .build();
    }

}
