package com.ecommerce.system.order.service.domain;

import com.ecommerce.system.order.service.domain.dto.message.PaymentResponse;
import com.ecommerce.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;

public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {
    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {

    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {

    }
}
