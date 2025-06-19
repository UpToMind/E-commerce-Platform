package com.ecommerce.system.seller.service.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "seller-service")
public class SellerServiceConfigData {
    private String sellerApprovalRequestTopicName;
    private String sellerApprovalResponseTopicName;
}
