package com.crafts.profileconsumer.service.impl;

import com.crafts.profileconsumer.dto.UserProfileDTO;
import com.crafts.profileconsumer.dto.UserProfileValidationResponseDTO;
import com.crafts.profileconsumer.enums.ValidationStatusEnum;
import com.crafts.profileconsumer.exception.KafkaProcessingException;
import com.crafts.profileconsumer.producer.UserProfileValidationResultKafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserProfileValidationServiceImplTest {

    private WebClient webClient;
    private WebClient.Builder webClientBuilder;
    private UserProfileValidationResultKafkaProducer kafkaProducer;
    private ProductRegistryServiceImpl productRegistryService;
    private ReactiveCircuitBreakerFactory cbFactory;

    private UserProfileValidationServiceImpl validationService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        webClientBuilder = mock(WebClient.Builder.class);
        kafkaProducer = mock(UserProfileValidationResultKafkaProducer.class);
        productRegistryService = mock(ProductRegistryServiceImpl.class);
        cbFactory = mock(ReactiveCircuitBreakerFactory.class);
        objectMapper = mock(ObjectMapper.class);

        when(webClientBuilder.build()).thenReturn(webClient);

        validationService = new UserProfileValidationServiceImpl(webClientBuilder, kafkaProducer, productRegistryService, cbFactory);
    }

    @Test
    void testValidateUserProfile_MessageIsNull() {
        validationService.validateUserProfile((String) null);
        verifyNoInteractions(webClient, kafkaProducer, productRegistryService, cbFactory);
    }

    @Test
    void testValidateUserProfile_MessageDeserializationFailure() {
        String message = "invalid_json_message";
        validationService.validateUserProfile(message);
        verifyNoInteractions(kafkaProducer, productRegistryService, cbFactory);
    }

    @Test
    void testValidateUserProfile_MissingProductValidationEndpoint() throws KafkaProcessingException {
        String missingProductId = "missingProduct";
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setSubscriptions(Collections.singletonList(missingProductId));

        when(productRegistryService.getProductValidationEndpoint(missingProductId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> validationService.validateUserProfile(userProfileDTO));

        verify(kafkaProducer, times(1)).send(anyString(), eq("VALIDATION_NOT_COMPLETED"), any());
    }

    @Test
    public void testAllValidationsSuccess() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setSubscriptions(List.of("product_1"));

        UserProfileValidationResponseDTO responseDTO = new UserProfileValidationResponseDTO();
        responseDTO.setProductId("product_1");
        CompletableFuture<UserProfileValidationResponseDTO> future = CompletableFuture.completedFuture(responseDTO);
        List<CompletableFuture<UserProfileValidationResponseDTO>> futures = Collections.singletonList(future);

        validationService.validateAndSend(futures, userProfileDTO);
        assertEquals(ValidationStatusEnum.SUCCESS.getStatus(), userProfileDTO.getConsolidatedStatus());
    }

    @Test
    public void testExceptionalFutureCompletion() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();

        CompletableFuture<UserProfileValidationResponseDTO> future = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Simulated error");
        });

        List<CompletableFuture<UserProfileValidationResponseDTO>> futures = Collections.singletonList(future);

        assertThrows(RuntimeException.class, () -> validationService.validateAndSend(futures, userProfileDTO));
    }


}