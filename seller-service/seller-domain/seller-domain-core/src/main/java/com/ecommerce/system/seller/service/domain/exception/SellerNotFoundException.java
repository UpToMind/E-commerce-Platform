package com.ecommerce.system.seller.service.domain.exception;

import com.ecommerce.system.domain.exception.DomainException;

public class SellerNotFoundException extends DomainException {
    public SellerNotFoundException(String message) {
        super(message);
    }

    public SellerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
