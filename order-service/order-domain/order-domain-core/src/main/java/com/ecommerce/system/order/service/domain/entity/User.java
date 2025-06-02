package com.ecommerce.system.order.service.domain.entity;

import com.ecommerce.system.domain.entity.AggregateRoot;
import com.ecommerce.system.domain.valueobject.UserId;

public class User extends AggregateRoot<UserId> {
    public User() {

    }

    public User(UserId userId) {
        super.setId(userId);
    }
}
