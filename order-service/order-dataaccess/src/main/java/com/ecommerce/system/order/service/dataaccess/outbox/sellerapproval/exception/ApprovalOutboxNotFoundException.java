package com.ecommerce.system.order.service.dataaccess.outbox.sellerapproval.exception;

public class ApprovalOutboxNotFoundException extends RuntimeException {
    public ApprovalOutboxNotFoundException(String message) {
        super(message);
    }
}
