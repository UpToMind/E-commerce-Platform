package com.ecommerce.system.seller.service.messaging.publisher.kafka;

import com.ecommerce.system.domain.event.publisher.DomainEventPublisher;
import com.ecommerce.system.kafka.order.avro.model.SellerApprovalResponseAvroModel;
import com.ecommerce.system.kafka.producer.KafkaMessageHelper;
import com.ecommerce.system.kafka.producer.service.KafkaProducer;
import com.ecommerce.system.seller.service.domain.config.SellerServiceConfigData;
import com.ecommerce.system.seller.service.domain.event.OrderRejectedEvent;
import com.ecommerce.system.seller.service.messaging.mapper.SellerMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderRejectedKafkaMessagePublisher implements DomainEventPublisher<OrderRejectedEvent> {

    private final SellerMessagingDataMapper sellerMessagingDataMapper;
    private final KafkaProducer<String, SellerApprovalResponseAvroModel> kafkaProducer;
    private final SellerServiceConfigData sellerServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public OrderRejectedKafkaMessagePublisher(SellerMessagingDataMapper sellerMessagingDataMapper,
                                              KafkaProducer<String, SellerApprovalResponseAvroModel> kafkaProducer,
                                              SellerServiceConfigData sellerServiceConfigData,
                                              KafkaMessageHelper kafkaMessageHelper) {
        this.sellerMessagingDataMapper = sellerMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.sellerServiceConfigData = sellerServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderRejectedEvent orderRejectedEvent) {
        String orderId = orderRejectedEvent.getOrderApproval().getOrderId().getValue().toString();

        log.info("Received OrderRejectedEvent for order id: {}", orderId);

        try {
            SellerApprovalResponseAvroModel sellerApprovalResponseAvroModel =
                    sellerMessagingDataMapper
                            .orderRejectedEventToSellerApprovalResponseAvroModel(orderRejectedEvent);

            kafkaProducer.send(sellerServiceConfigData.getSellerApprovalResponseTopicName(),
                    orderId,
                    sellerApprovalResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(sellerServiceConfigData
                                    .getSellerApprovalResponseTopicName(),
                            sellerApprovalResponseAvroModel,
                            orderId,
                            "SellerApprovalResponseAvroModel"));

            log.info("SellerApprovalResponseAvroModel sent to kafka at: {}", System.nanoTime());
        } catch (Exception e) {
            log.error("Error while sending SellerApprovalResponseAvroModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }

}

