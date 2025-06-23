package com.ecommerce.system.seller.service.domain.ports.output.repository;

import com.ecommerce.system.seller.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {
    OrderApproval save(OrderApproval orderApproval);
}
