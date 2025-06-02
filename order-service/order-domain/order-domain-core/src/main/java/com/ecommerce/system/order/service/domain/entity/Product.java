package com.ecommerce.system.order.service.domain.entity;

import com.ecommerce.system.domain.entity.BaseEntity;
import com.ecommerce.system.domain.valueobject.Money;
import com.ecommerce.system.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;

    public Product(ProductId productId,String name, Money price) {
        super.setId(productId);
        this.name = name;
        this.price = price;
    }

    public Product(ProductId productId) {
        super.setId(productId);
    }

    public void updateWithConfirmedNameAndPrice(String name, Money price) {
        this.name = name;
        this.price = price;
    }

    public Money getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
