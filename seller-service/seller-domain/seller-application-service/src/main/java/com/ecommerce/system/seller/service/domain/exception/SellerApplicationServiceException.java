package com.ecommerce.system.seller.service.domain.exception;

import com.ecommerce.system.domain.exception.DomainException;

public class SellerApplicationServiceException extends DomainException {
    public SellerApplicationServiceException(String message) {
        super(message);
    }

    public SellerApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
