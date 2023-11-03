package com.crafts.profileconsumer.producer;

import com.crafts.profileconsumer.config.props.KafkaPropsConfig;
import com.crafts.profileconsumer.exception.KafkaProcessingException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class UserProfileValidationResultKafkaProducer {
    private final KafkaPropsConfig kafkaPropsConfig;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserProfileValidationResultKafkaProducer(KafkaPropsConfig kafkaPropsConfig, @Qualifier("userProfileValidationResultKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaPropsConfig = kafkaPropsConfig;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Retry(name = "kafka-producer-retry", fallbackMethod = "sendFallback")
    public <T> void send(String message, String eventType, String key) throws KafkaProcessingException {
        String userProfileValidationResultTopic = kafkaPropsConfig.getUserProfileValidationResultTopic();
        if (null == userProfileValidationResultTopic) {
            log.error("user-profile-validation-result topic is not available ignoring message {}, Event type {}", message, eventType);
            return;
        }
        try {
            List<Header> headers = Arrays.asList(new RecordHeader("EVENT_TYPE", eventType.getBytes()), new RecordHeader("USER_ID", key.getBytes()));
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
                    userProfileValidationResultTopic, null, null, null, message, headers);
            log.info("Sending message to Topic: {}, Event type {}", userProfileValidationResultTopic, eventType);
            kafkaTemplate.send(producerRecord);
        } catch (Exception e) {
            log.info("Exception in message to Topic: {}, Event type {}", userProfileValidationResultTopic, eventType);
            throw new KafkaProcessingException("Error while sending message to :" + userProfileValidationResultTopic, e);
        }
    }

    // Fallback method
    public <T> void sendFallback(String message, String eventType, String key, Exception e) {
        log.error("Failed to send message after validation for userId: {}. Reason: {}", key, e.getMessage());
    }
}
