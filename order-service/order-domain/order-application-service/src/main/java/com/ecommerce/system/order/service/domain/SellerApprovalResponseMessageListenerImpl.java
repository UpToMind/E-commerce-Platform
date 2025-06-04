package com.ecommerce.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class SellerApprovalResponseMessageListenerImpl implements SellerApprovalResponseMessageListener{
    @Override
    public void orderApproved(SellerApprovalResponse sellerApprovalResponse) {

    }

    @Override
    public void orderRejected(SellerApprovalResponse sellerApprovalResponse) {

    }
}
