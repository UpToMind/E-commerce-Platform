package com.ecommerce.system.order.service.domain;

import com.ecommerce.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ecommerce.system.order.service.domain.dto.create.CreateOrderResponse;
import com.ecommerce.system.order.service.domain.dto.track.TrackOrderQuery;
import com.ecommerce.system.order.service.domain.dto.track.TrackOrderResponse;
import com.ecommerce.system.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
class OrderApplicationServiceImpl implements OrderApplicationService {
    @Override
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        return null;
    }

    @Override
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return null;
    }
}
