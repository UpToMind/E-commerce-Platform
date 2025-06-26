package com.ecommerce.system.seller.service.domain;

import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.seller.service.domain.entity.Seller;
import com.ecommerce.system.seller.service.domain.event.OrderApprovalEvent;
import com.ecommerce.system.seller.service.domain.event.OrderApprovedEvent;
import com.ecommerce.system.seller.service.domain.event.OrderRejectedEvent;

import java.util.List;

public interface SellerDomainService {

    OrderApprovalEvent validateOrder(Seller seller,
                                     List<String> failureMessages);
}
