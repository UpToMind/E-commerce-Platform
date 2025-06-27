package com.ecommerce.system.seller.service.domain;

import com.ecommerce.system.seller.service.domain.dto.SellerApprovalRequest;
import com.ecommerce.system.seller.service.domain.event.OrderApprovalEvent;
import com.ecommerce.system.seller.service.domain.ports.input.message.listener.SellerApprovalRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SellerApprovalRequestMessageListenerImpl implements SellerApprovalRequestMessageListener {
    private final SellerApprovalRequestHelper sellerApprovalRequestHelper;

    public SellerApprovalRequestMessageListenerImpl(SellerApprovalRequestHelper
                                                                sellerApprovalRequestHelper) {
        this.sellerApprovalRequestHelper = sellerApprovalRequestHelper;
    }

    @Override
    public void approveOrder(SellerApprovalRequest sellerApprovalRequest) {
        sellerApprovalRequestHelper.persistOrderApproval(sellerApprovalRequest);
    }
}
