package com.ecommerce.system.seller.service.domain;

import com.ecommerce.system.domain.valueobject.OrderId;
import com.ecommerce.system.seller.service.domain.dto.SellerApprovalRequest;
import com.ecommerce.system.seller.service.domain.entity.Seller;
import com.ecommerce.system.seller.service.domain.event.OrderApprovalEvent;
import com.ecommerce.system.seller.service.domain.exception.SellerNotFoundException;
import com.ecommerce.system.seller.service.domain.mapper.SellerDataMapper;
import com.ecommerce.system.seller.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.ecommerce.system.seller.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import com.ecommerce.system.seller.service.domain.ports.output.repository.OrderApprovalRepository;
import com.ecommerce.system.seller.service.domain.ports.output.repository.SellerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class SellerApprovalRequestHelper {
    private final SellerDomainService sellerDomainService;
    private final SellerDataMapper sellerDataMapper;
    private final SellerRepository sellerRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    public SellerApprovalRequestHelper(SellerDomainService sellerDomainService,
                                           SellerDataMapper sellerDataMapper,
                                           SellerRepository sellerRepository,
                                           OrderApprovalRepository orderApprovalRepository,
                                           OrderApprovedMessagePublisher orderApprovedMessagePublisher,
                                           OrderRejectedMessagePublisher orderRejectedMessagePublisher) {
        this.sellerDomainService = sellerDomainService;
        this.sellerDataMapper = sellerDataMapper;
        this.sellerRepository = sellerRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.orderApprovedMessagePublisher = orderApprovedMessagePublisher;
        this.orderRejectedMessagePublisher = orderRejectedMessagePublisher;
    }

    @Transactional
    public OrderApprovalEvent persistOrderApproval(SellerApprovalRequest sellerApprovalRequest) {
        log.info("Processing seller approval for order id: {}", sellerApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Seller seller = findSeller(sellerApprovalRequest);
        OrderApprovalEvent orderApprovalEvent =
                sellerDomainService.validateOrder(
                        seller,
                        failureMessages,
                        orderApprovedMessagePublisher,
                        orderRejectedMessagePublisher);
        orderApprovalRepository.save(seller.getOrderApproval());
        return orderApprovalEvent;
    }

    private Seller findSeller(SellerApprovalRequest sellerApprovalRequest) {
        Seller seller = sellerDataMapper
                .sellerApprovalRequestToSeller(sellerApprovalRequest);
        Optional<Seller> sellerResult = sellerRepository.findSellerInformation(seller);
        if (sellerResult.isEmpty()) {
            log.error("Seller with id " + seller.getId().getValue() + " not found!");
            throw new SellerNotFoundException("Seller with id " + seller.getId().getValue() +
                    " not found!");
        }

        Seller sellerEntity = sellerResult.get();
        seller.getOrderDetail().getProducts().forEach(product ->
                sellerEntity.getOrderDetail().getProducts().forEach(p -> {
                    if (p.getId().equals(product.getId())) {
                        product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
                    }
                }));
        seller.getOrderDetail().setId(new OrderId(UUID.fromString(sellerApprovalRequest.getOrderId())));

        return seller;
    }
}
