package com.ecommerce.system.seller.service.dataaccess.seller.adapter;

import com.ecommerce.system.dataaccess.seller.entity.SellerEntity;
import com.ecommerce.system.dataaccess.seller.repository.SellerJpaRepository;
import com.ecommerce.system.seller.service.dataaccess.seller.mapper.SellerDataAccessMapper;
import com.ecommerce.system.seller.service.domain.entity.Seller;
import com.ecommerce.system.seller.service.domain.ports.output.repository.SellerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
