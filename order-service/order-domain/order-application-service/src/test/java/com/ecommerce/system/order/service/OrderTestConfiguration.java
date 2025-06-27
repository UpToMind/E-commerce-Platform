package com.ecommerce.system.order.service;

import com.ecommerce.system.order.service.domain.OrderDomainService;
import com.ecommerce.system.order.service.domain.OrderDomainServiceImpl;
import com.ecommerce.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.ecommerce.system.order.service.domain.ports.output.message.publisher.sellerapproval.SellerApprovalRequestMessagePublisher;
import com.ecommerce.system.order.service.domain.ports.output.repository.*;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.ecommerce.system")
public class OrderTestConfiguration {
    @Bean
    public PaymentRequestMessagePublisher paymentRequestMessagePublisher() {
        return Mockito.mock(PaymentRequestMessagePublisher.class);
    }

    @Bean
    public SellerApprovalRequestMessagePublisher sellerApprovalRequestMessagePublisher() {
        return Mockito.mock(SellerApprovalRequestMessagePublisher.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public SellerRepository sellerRepository() {
        return Mockito.mock(SellerRepository.class);
    }

    @Bean
    public PaymentOutboxRepository paymentOutboxRepository() {
        return Mockito.mock(PaymentOutboxRepository.class);
    }

    @Bean
    public ApprovalOutboxRepository approvalOutboxRepository() {
        return Mockito.mock(ApprovalOutboxRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
