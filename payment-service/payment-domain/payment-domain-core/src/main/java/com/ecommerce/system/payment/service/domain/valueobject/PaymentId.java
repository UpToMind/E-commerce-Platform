package com.ecommerce.system.payment.service.domain.valueobject;

import com.ecommerce.system.domain.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
