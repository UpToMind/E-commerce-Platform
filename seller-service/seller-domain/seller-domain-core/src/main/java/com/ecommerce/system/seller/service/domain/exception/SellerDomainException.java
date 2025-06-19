package com.ecommerce.system.seller.service.domain.exception;

import com.ecommerce.system.domain.exception.DomainException;

public class SellerDomainException extends DomainException {
    public SellerDomainException(String message) {
        super(message);
    }

    public SellerDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
