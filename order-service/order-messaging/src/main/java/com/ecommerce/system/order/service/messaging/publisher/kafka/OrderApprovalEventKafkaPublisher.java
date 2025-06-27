package com.ecommerce.system.order.service.messaging.publisher.kafka;

import com.ecommerce.system.kafka.order.avro.model.SellerApprovalRequestAvroModel;
import com.ecommerce.system.kafka.producer.KafkaMessageHelper;
import com.ecommerce.system.kafka.producer.service.KafkaProducer;
import com.ecommerce.system.order.service.domain.config.OrderServiceConfigData;
import com.ecommerce.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.ecommerce.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.ecommerce.system.order.service.domain.ports.output.message.publisher.sellerapproval.SellerApprovalRequestMessagePublisher;
import com.ecommerce.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.ecommerce.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderApprovalEventKafkaPublisher implements SellerApprovalRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, SellerApprovalRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public OrderApprovalEventKafkaPublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                            KafkaProducer<String, SellerApprovalRequestAvroModel> kafkaProducer,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaMessageHelper kafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }


    @Override
    public void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                        BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback) {
        OrderApprovalEventPayload orderApprovalEventPayload =
                kafkaMessageHelper.getOrderEventPayload(orderApprovalOutboxMessage.getPayload(),
                        OrderApprovalEventPayload.class);

        String sagaId = orderApprovalOutboxMessage.getSagaId().toString();

        log.info("Received OrderApprovalOutboxMessage for order id: {} and saga id: {}",
                orderApprovalEventPayload.getOrderId(),
                sagaId);

        try {
            SellerApprovalRequestAvroModel sellerApprovalRequestAvroModel =
                    orderMessagingDataMapper
                            .orderApprovalEventToSellerApprovalRequestAvroModel(sagaId,
                                    orderApprovalEventPayload);

            kafkaProducer.send(orderServiceConfigData.getSellerApprovalRequestTopicName(),
                    sagaId,
                    sellerApprovalRequestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getSellerApprovalRequestTopicName(),
                            sellerApprovalRequestAvroModel,
                            orderApprovalOutboxMessage,
                            outboxCallback,
                            orderApprovalEventPayload.getOrderId(),
                            "SellerApprovalRequestAvroModel"));

            log.info("OrderApprovalEventPayload sent to kafka for order id: {} and saga id: {}",
                    sellerApprovalRequestAvroModel.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending OrderApprovalEventPayload to kafka for order id: {} and saga id: {}," +
                    " error: {}", orderApprovalEventPayload.getOrderId(), sagaId, e.getMessage());
        }


    }
}

