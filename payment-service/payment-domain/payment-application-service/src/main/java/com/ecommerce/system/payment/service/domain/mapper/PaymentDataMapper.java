package com.ecommerce.system.payment.service.domain.mapper;

import com.ecommerce.system.domain.valueobject.Money;
import com.ecommerce.system.domain.valueobject.OrderId;
import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.payment.service.domain.dto.PaymentRequest;
import com.ecommerce.system.payment.service.domain.entity.Payment;
import com.ecommerce.system.payment.service.domain.event.PaymentEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .userId(new UserId(UUID.fromString(paymentRequest.getUserId())))
                .price(new Money(paymentRequest.getPrice()))
                .build();
    }
}
