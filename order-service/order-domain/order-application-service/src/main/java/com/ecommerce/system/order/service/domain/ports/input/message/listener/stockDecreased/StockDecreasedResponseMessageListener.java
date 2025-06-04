package com.ecommerce.system.order.service.domain.ports.input.message.listener.stockDecreased;

import com.ecommerce.system.order.service.domain.dto.message.SellerApprovalResponse;

public interface StockDecreasedResponseMessageListener {
    void StockDecreased(SellerApprovalResponse sellerApprovalResponse);

    void StockDecreasedFailed(SellerApprovalResponse sellerApprovalResponse);
    
}
