package com.ecommerce.system.seller.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "com.ecommerce.system.seller.service.dataaccess", "com.ecommerce.system.dataaccess" })
@EntityScan(basePackages = { "com.ecommerce.system.seller.service.dataaccess", "com.ecommerce.system.dataaccess" })
@SpringBootApplication(scanBasePackages = "com.ecommerce.system")
public class SellerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SellerServiceApplication.class, args);
    }
}
