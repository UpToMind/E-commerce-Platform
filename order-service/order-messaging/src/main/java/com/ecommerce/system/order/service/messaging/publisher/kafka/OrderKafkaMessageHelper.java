package com.ecommerce.system.order.service.messaging.publisher.kafka;

import com.ecommerce.system.kafka.order.avro.model.PaymentRequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderKafkaMessageHelper {

    public BiConsumer<SendResult<String, PaymentRequestAvroModel>, Throwable> getKafkaCallback(
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
