package com.ecommerce.system.order.service;

import com.ecommerce.system.order.service.domain.OrderDomainService;
import com.ecommerce.system.order.service.domain.OrderDomainServiceImpl;
import com.ecommerce.system.order.service.domain.ports.output.repository.OrderRepository;
import com.ecommerce.system.order.service.domain.ports.output.repository.SellerRepository;
import com.ecommerce.system.order.service.domain.ports.output.repository.UserRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.ecommerce.system")
public class OrderTestConfiguration {
    @Bean
    public OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderCreatedPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderPaidSellerRequestMessagePublisher orderPaidSellerRequestMessagePublisher() {
        return Mockito.mock(OrderPaidSellerRequestMessagePublisher.class);
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
    public SellerRepository restaurantRepository() {
        return Mockito.mock(SellerRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
