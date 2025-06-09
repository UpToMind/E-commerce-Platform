package com.ecommerce.system.order.service.messaging.publisher.kafka;

import com.ecommerce.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.ecommerce.system.kafka.producer.service.KafkaProducer;
import com.ecommerce.system.order.service.domain.config.OrderServiceConfigData;
import com.ecommerce.system.order.service.domain.event.OrderCancelledEvent;
import com.ecommerce.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.ecommerce.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

    public CancelOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void publish(OrderCancelledEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCancelledEvent for order id: {}", orderId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper
                    .orderCancelledEventToPaymentRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel,
                    getKafkaCallback(orderServiceConfigData.getPaymentResponseTopicName(), paymentRequestAvroModel));

            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }

    private BiConsumer<SendResult<String, PaymentRequestAvroModel>, Throwable> getKafkaCallback(
            String paymentResponseTopicName,
            PaymentRequestAvroModel paymentRequestAvroModel) {

        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending PaymentRequestAvroModel message {} to topic {}",
                        paymentRequestAvroModel, paymentResponseTopicName, ex);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for PaymentRequestAvroModel: {} " +
                                "Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        paymentRequestAvroModel,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
            }
        };
    }
}
