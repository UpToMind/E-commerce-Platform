package com.ecommerce.system.seller.service.dataaccess.seller.adapter;

import com.ecommerce.system.seller.service.dataaccess.seller.mapper.SellerDataAccessMapper;
import com.ecommerce.system.seller.service.dataaccess.seller.repository.OrderApprovalJpaRepository;
import com.ecommerce.system.seller.service.domain.entity.OrderApproval;
import com.ecommerce.system.seller.service.domain.ports.output.repository.OrderApprovalRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final SellerDataAccessMapper sellerDataAccessMapper;

    public OrderApprovalRepositoryImpl(OrderApprovalJpaRepository orderApprovalJpaRepository,
                                       SellerDataAccessMapper sellerDataAccessMapper) {
        this.orderApprovalJpaRepository = orderApprovalJpaRepository;
        this.sellerDataAccessMapper = sellerDataAccessMapper;
    }

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        return sellerDataAccessMapper
                .orderApprovalEntityToOrderApproval(orderApprovalJpaRepository
                        .save(sellerDataAccessMapper.orderApprovalToOrderApprovalEntity(orderApproval)));
    }

}
