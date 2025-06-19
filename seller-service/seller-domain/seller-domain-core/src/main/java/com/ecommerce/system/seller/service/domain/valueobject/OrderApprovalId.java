package com.ecommerce.system.seller.service.domain.valueobject;

import com.ecommerce.system.domain.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {
    public OrderApprovalId(UUID value) {
        super(value);
    }
}
