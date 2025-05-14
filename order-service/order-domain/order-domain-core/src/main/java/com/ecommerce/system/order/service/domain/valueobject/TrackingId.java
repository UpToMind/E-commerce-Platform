package com.ecommerce.system.order.service.domain.valueobject;

import com.ecommerce.system.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID value) {
        super(value);
    }
}
