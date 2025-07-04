package com.ecommerce.system.order.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "com.ecommerce.system.order.service.dataaccess", "com.ecommerce.system.dataaccess" })
@EntityScan(basePackages = { "com.ecommerce.system.order.service.dataaccess", "com.ecommerce.system.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.ecommerce.system")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
