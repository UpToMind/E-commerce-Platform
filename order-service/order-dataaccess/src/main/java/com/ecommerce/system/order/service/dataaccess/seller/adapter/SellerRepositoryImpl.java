package com.ecommerce.system.order.service.dataaccess.seller.adapter;

import com.ecommerce.system.order.service.dataaccess.seller.entity.SellerEntity;
import com.ecommerce.system.order.service.dataaccess.seller.mapper.SellerDataAccessMapper;
import com.ecommerce.system.order.service.dataaccess.seller.repository.SellerJpaRepository;
import com.ecommerce.system.order.service.domain.entity.Seller;
import com.ecommerce.system.order.service.domain.ports.output.repository.SellerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SellerRepositoryImpl implements SellerRepository {
    private final SellerJpaRepository sellerJpaRepository;
    private final SellerDataAccessMapper sellerDataAccessMapper;

    public SellerRepositoryImpl(SellerJpaRepository sellerJpaRepository,
                                    SellerDataAccessMapper sellerDataAccessMapper) {
        this.sellerJpaRepository = sellerJpaRepository;
        this.sellerDataAccessMapper = sellerDataAccessMapper;
    }

    @Override
    public Optional<Seller> findSellerInformation(Seller seller) {
        List<UUID> sellerProducts =
                sellerDataAccessMapper.sellerToSellerProducts(seller);
        Optional<List<SellerEntity>> sellerEntities = sellerJpaRepository
                .findBySellerIdAndProductIdIn(seller.getId().getValue(),
                        sellerProducts);
        return sellerEntities.map(sellerDataAccessMapper::sellerEntityToSeller);
    }
    
}
