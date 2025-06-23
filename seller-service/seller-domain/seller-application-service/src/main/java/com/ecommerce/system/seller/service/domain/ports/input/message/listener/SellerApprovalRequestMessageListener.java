package com.ecommerce.system.seller.service.domain.ports.input.message.listener;

import com.ecommerce.system.seller.service.domain.dto.SellerApprovalRequest;

public interface SellerApprovalRequestMessageListener {
    void approveOrder(SellerApprovalRequest sellerApprovalRequest);
}
