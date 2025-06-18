package com.ecommerce.system.payment.service.domain.valueobject;

import com.ecommerce.system.domain.valueobject.BaseId;

import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {
    public CreditEntryId(UUID value) {
        super(value);
    }
}

