package com.ecommerce.system.payment.service.domain;

import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.payment.service.domain.dto.PaymentRequest;
import com.ecommerce.system.payment.service.domain.entity.CreditEntry;
import com.ecommerce.system.payment.service.domain.entity.CreditHistory;
import com.ecommerce.system.payment.service.domain.entity.Payment;
import com.ecommerce.system.payment.service.domain.event.PaymentEvent;
import com.ecommerce.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.ecommerce.system.payment.service.domain.mapper.PaymentDataMapper;
import com.ecommerce.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.ecommerce.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.ecommerce.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.ecommerce.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.ecommerce.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.ecommerce.system.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {
    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher;
    private final PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher;
    private final PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher;

    public PaymentRequestHelper(PaymentDomainService paymentDomainService,
                                PaymentDataMapper paymentDataMapper,
                                PaymentRepository paymentRepository,
                                CreditEntryRepository creditEntryRepository,
                                CreditHistoryRepository creditHistoryRepository,
                                PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher,
                                PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher,
                                PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher) {
        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
        this.paymentCancelledEventDomainEventPublisher = paymentCancelledEventDomainEventPublisher;
        this.paymentFailedEventDomainEventPublisher = paymentFailedEventDomainEventPublisher;
    }

    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getUserId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getUserId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent =
                paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages,
                        paymentCompletedEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
        return paymentEvent;
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());
        Optional<Payment> paymentResponse = paymentRepository
                .findByOrderId(UUID.fromString(paymentRequest.getOrderId()));
        if (paymentResponse.isEmpty()) {
            log.error("Payment with order id: {} could not be found!", paymentRequest.getOrderId());
            throw new PaymentApplicationServiceException("Payment with order id: " +
                    paymentRequest.getOrderId() + " could not be found!");
        }
        Payment payment = paymentResponse.get();
        CreditEntry creditEntry = getCreditEntry(payment.getUserId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getUserId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService
                .validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages,
                        paymentCancelledEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
        return paymentEvent;
    }

    private CreditEntry getCreditEntry(UserId userId) {
        Optional<CreditEntry> creditEntry = creditEntryRepository.findByUserId(userId);
        if (creditEntry.isEmpty()) {
            log.error("Could not find credit entry for user: {}", userId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for user: " +
                    userId.getValue());
        }
        return creditEntry.get();
    }

    private List<CreditHistory> getCreditHistory(UserId userId) {
        Optional<List<CreditHistory>> creditHistories = creditHistoryRepository.findByUserId(userId);
        if (creditHistories.isEmpty()) {
            log.error("Could not find credit history for user: {}", userId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit history for user: " +
                    userId.getValue());
        }
        return creditHistories.get();
    }

    private void persistDbObjects(Payment payment,
                                  CreditEntry creditEntry,
                                  List<CreditHistory> creditHistories,
                                  List<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }
}
