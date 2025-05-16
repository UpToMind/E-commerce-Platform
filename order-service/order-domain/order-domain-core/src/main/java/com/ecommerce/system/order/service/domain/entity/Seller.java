package com.ecommerce.system.order.service.domain.entity;

import com.ecommerce.system.domain.entity.AggregateRoot;
import com.ecommerce.system.domain.valueobject.SellerId;

import java.util.List;

public class Seller extends AggregateRoot<SellerId> {
    private final List<Product> products;

    private Seller(Builder builder) {
        super.setId(builder.sellerId);
        products = builder.products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public static final class Builder {
        private SellerId sellerId;
        private List<Product> products;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder sellerId(SellerId val) {
            sellerId = val;
            return this;
        }

        public Builder products(List<Product> val) {
            products = val;
            return this;
        }

        public Seller build() {
            return new Seller(this);
        }
    }
}
