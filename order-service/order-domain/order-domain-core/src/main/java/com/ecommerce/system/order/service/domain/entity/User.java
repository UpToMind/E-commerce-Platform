package com.ecommerce.system.order.service.domain.entity;

import com.ecommerce.system.domain.entity.AggregateRoot;
import com.ecommerce.system.domain.valueobject.UserId;

public class User extends AggregateRoot<UserId> {
    private String username;
    private String firstName;
    private String lastName;

    public User(UserId userId, String username, String firstName, String lastName) {
        super.setId(userId);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(UserId userId) {
        super.setId(userId);
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
