package com.ecommerce.system.seller.service.domain;


import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.domain.valueobject.OrderApprovalStatus;
import com.ecommerce.system.seller.service.domain.entity.Seller;
import com.ecommerce.system.seller.service.domain.event.OrderApprovalEvent;
import com.ecommerce.system.seller.service.domain.event.OrderApprovedEvent;
import com.ecommerce.system.seller.service.domain.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.ecommerce.system.domain.DomainConstants.UTC;

@Slf4j
public class SellerDomainServiceImpl implements SellerDomainService {

    @Override
    public OrderApprovalEvent validateOrder(Seller seller, List<String> failureMessages) {
        seller.validateOrder(failureMessages);
        log.info("Validating order with id: {}", seller.getOrderDetail().getId().getValue());

        if (failureMessages.isEmpty()) {
            log.info("Order is approved for order id: {}", seller.getOrderDetail().getId().getValue());
            seller.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(seller.getOrderApproval(),
                    seller.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC)));
        } else {
            log.info("Order is rejected for order id: {}", seller.getOrderDetail().getId().getValue());
            seller.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(seller.getOrderApproval(),
                    seller.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC)));
        }
    }
}
