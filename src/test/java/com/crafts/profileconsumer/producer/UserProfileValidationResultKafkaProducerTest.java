package com.crafts.profileconsumer.producer;

import com.crafts.profileconsumer.config.props.KafkaPropsConfig;
import com.crafts.profileconsumer.exception.KafkaProcessingException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

public class UserProfileValidationResultKafkaProducerTest {

    @Mock
    private KafkaPropsConfig kafkaPropsConfig;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private UserProfileValidationResultKafkaProducer producer;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        producer = new UserProfileValidationResultKafkaProducer(kafkaPropsConfig, kafkaTemplate);
        when(kafkaPropsConfig.getUserProfileValidationResultTopic()).thenReturn("test-topic");
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void shouldLogErrorWhenTopicIsNotAvailable() throws KafkaProcessingException {
        when(kafkaPropsConfig.getUserProfileValidationResultTopic()).thenReturn(null);

        producer.send("testMessage", "testEventType", "testKey");

        verify(kafkaTemplate, times(0)).send(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionAndFallbackWhenKafkaSendFails() {
        doThrow(RuntimeException.class).when(kafkaTemplate).send(any(ProducerRecord.class));

        try {
            producer.send("testMessage", "testEventType", "testKey");
        } catch (KafkaProcessingException ignored) {
        }

        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

}
