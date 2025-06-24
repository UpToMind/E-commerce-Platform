package com.ecommerce.system.order.service.domain;

import com.ecommerce.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ecommerce.system.order.service.domain.entity.Order;
import com.ecommerce.system.order.service.domain.entity.Seller;
import com.ecommerce.system.order.service.domain.entity.User;
import com.ecommerce.system.order.service.domain.event.OrderCreatedEvent;
import com.ecommerce.system.order.service.domain.exception.OrderDomainException;
import com.ecommerce.system.order.service.domain.mapper.OrderDataMapper;
import com.ecommerce.system.order.service.domain.ports.output.repository.OrderRepository;
import com.ecommerce.system.order.service.domain.ports.output.repository.SellerRepository;
import com.ecommerce.system.order.service.domain.ports.output.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {
    private final OrderDomainService orderDomainService;

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final SellerRepository sellerRepository;

    private final OrderDataMapper orderDataMapper;

    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedEventDomainEventPublisher;

    public OrderCreateHelper(OrderDomainService orderDomainService,
                             OrderRepository orderRepository,
                             UserRepository userRepository,
                             SellerRepository sellerRepository,
                             OrderDataMapper orderDataMapper,
                             OrderCreatedPaymentRequestMessagePublisher orderCreatedEventDomainEventPublisher) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.sellerRepository = sellerRepository;
        this.orderDataMapper = orderDataMapper;
        this.orderCreatedEventDomainEventPublisher = orderCreatedEventDomainEventPublisher;
    }

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkUser(createOrderCommand.getUserId());
        Seller seller = checkSeller(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, seller, orderCreatedEventDomainEventPublisher);
        saveOrder(order);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }

    private Seller checkSeller(CreateOrderCommand createOrderCommand) {
        Seller seller = orderDataMapper.createOrderCommandToSeller(createOrderCommand);
        Optional<Seller> optionalRestaurant = sellerRepository.findSellerInformation(seller);
        if (optionalRestaurant.isEmpty()) {
            log.warn("Could not find restaurant with restaurant id: {}", createOrderCommand.getSellerId());
            throw new OrderDomainException("Could not find restaurant with restaurant id: " +
                    createOrderCommand.getSellerId());
        }
        return optionalRestaurant.get();
    }

    private void checkUser(UUID userId) {
        Optional<User> user = userRepository.findUser(userId);
        if (user.isEmpty()) {
            log.warn("Could not find user with user id: {}", userId);
            throw new OrderDomainException("Could not find user with user id: " + user);
        }
    }

    private Order saveOrder(Order order) {
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", orderResult.getId().getValue());
        return orderResult;
    }
}
