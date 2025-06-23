package com.ecommerce.system.seller.service.messaging.listener.kafka;

import com.ecommerce.system.kafka.consumer.KafkaConsumer;
import com.ecommerce.system.kafka.order.avro.model.SellerApprovalRequestAvroModel;
import com.ecommerce.system.seller.service.domain.ports.input.message.listener.SellerApprovalRequestMessageListener;
import com.ecommerce.system.seller.service.messaging.mapper.SellerMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SellerApprovalRequestKafkaListener implements KafkaConsumer<SellerApprovalRequestAvroModel> {

    private final SellerApprovalRequestMessageListener sellerApprovalRequestMessageListener;
    private final SellerMessagingDataMapper sellerMessagingDataMapper;

    public SellerApprovalRequestKafkaListener(SellerApprovalRequestMessageListener
                                                          sellerApprovalRequestMessageListener,
                                                  SellerMessagingDataMapper
                                                          sellerMessagingDataMapper) {
        this.sellerApprovalRequestMessageListener = sellerApprovalRequestMessageListener;
        this.sellerMessagingDataMapper = sellerMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.seller-approval-consumer-group-id}",
            topics = "${seller-service.seller-approval-request-topic-name}")
    public void receive(@Payload List<SellerApprovalRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of orders approval requests received with keys {}, partitions {} and offsets {}" +
                        ", sending for seller approval",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(sellerApprovalRequestAvroModel -> {
            log.info("Processing order approval for order id: {}", sellerApprovalRequestAvroModel.getOrderId());
            sellerApprovalRequestMessageListener.approveOrder(sellerMessagingDataMapper.
                    sellerApprovalRequestAvroModelToSellerApproval(sellerApprovalRequestAvroModel));
        });
    }

}
