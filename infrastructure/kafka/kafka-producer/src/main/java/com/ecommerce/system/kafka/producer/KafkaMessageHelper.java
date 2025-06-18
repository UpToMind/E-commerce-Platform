package com.ecommerce.system.kafka.producer;

import com.ecommerce.system.order.service.domain.exception.OrderDomainException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper {

    public <T> BiConsumer<SendResult<String, T>, Throwable>
    getKafkaCallback(String responseTopicName, T avroModel, String orderId, String avroModelName) {
        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending {} message {} to topic {}",
                        avroModelName, avroModel.toString(), responseTopicName, ex);
            } else if (result != null && result.getRecordMetadata() != null) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for order id: {} " +
                                "Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
            } else {
                log.warn("Kafka send succeeded but metadata is null for order id: {}", orderId);
            }
        };
    }
}
