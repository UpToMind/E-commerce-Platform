package com.ecommerce.system.seller.service.messaging.publisher.kafka;

import com.ecommerce.system.kafka.order.avro.model.SellerApprovalResponseAvroModel;
import com.ecommerce.system.kafka.producer.KafkaMessageHelper;
import com.ecommerce.system.kafka.producer.service.KafkaProducer;
import com.ecommerce.system.outbox.OutboxStatus;
import com.ecommerce.system.seller.service.domain.config.SellerServiceConfigData;
import com.ecommerce.system.seller.service.domain.outbox.model.OrderEventPayload;
import com.ecommerce.system.seller.service.domain.outbox.model.OrderOutboxMessage;
import com.ecommerce.system.seller.service.domain.ports.output.message.publisher.SellerApprovalResponseMessagePublisher;
import com.ecommerce.system.seller.service.messaging.mapper.SellerMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class SellerApprovalEventKafkaPublisher implements SellerApprovalResponseMessagePublisher {

    private final SellerMessagingDataMapper sellerMessagingDataMapper;
    private final KafkaProducer<String, SellerApprovalResponseAvroModel> kafkaProducer;
    private final SellerServiceConfigData sellerServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public SellerApprovalEventKafkaPublisher(SellerMessagingDataMapper dataMapper,
                                                 KafkaProducer<String, SellerApprovalResponseAvroModel>
                                                         kafkaProducer,
                                                 SellerServiceConfigData sellerServiceConfigData,
                                                 KafkaMessageHelper kafkaMessageHelper) {
        this.sellerMessagingDataMapper = dataMapper;
        this.kafkaProducer = kafkaProducer;
        this.sellerServiceConfigData = sellerServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }


    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage,
                        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        OrderEventPayload orderEventPayload =
                kafkaMessageHelper.getOrderEventPayload(orderOutboxMessage.getPayload(),
                        OrderEventPayload.class);

        String sagaId = orderOutboxMessage.getSagaId().toString();

        log.info("Received OrderOutboxMessage for order id: {} and saga id: {}",
                orderEventPayload.getOrderId(),
                sagaId);
        try {
            SellerApprovalResponseAvroModel sellerApprovalResponseAvroModel =
                    sellerMessagingDataMapper
                            .orderEventPayloadToSellerApprovalResponseAvroModel(sagaId, orderEventPayload);

            kafkaProducer.send(sellerServiceConfigData.getSellerApprovalResponseTopicName(),
                    sagaId,
                    sellerApprovalResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(sellerServiceConfigData
                                    .getSellerApprovalResponseTopicName(),
                            sellerApprovalResponseAvroModel,
                            orderOutboxMessage,
                            outboxCallback,
                            orderEventPayload.getOrderId(),
                            "SellerApprovalResponseAvroModel"));

            log.info("SellerApprovalResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                    sellerApprovalResponseAvroModel.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending SellerApprovalResponseAvroModel message" +
                            " to kafka with order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage());
        }
    }

}
