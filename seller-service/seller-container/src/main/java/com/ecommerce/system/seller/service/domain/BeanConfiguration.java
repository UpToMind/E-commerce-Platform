package com.ecommerce.system.seller.service.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public SellerDomainService sellerDomainService() {
        return new SellerDomainServiceImpl();
    }

}
