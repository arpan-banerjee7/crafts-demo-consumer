package com.crafts.profileservice.service.impl;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.crafts.profileservice.dto.SubscriptionRequestDTO;
import com.crafts.profileservice.dto.UserProfileDTO;
import com.crafts.profileservice.dto.UserProfileValidationResultDTO;
import com.crafts.profileservice.entity.UserProfileEO;
import com.crafts.profileservice.enums.ValidationStatusEnum;
import com.crafts.profileservice.exception.KafkaProcessingException;
import com.crafts.profileservice.exception.UserProfileBusinessException;
import com.crafts.profileservice.exception.UserProfileRepositoryException;
import com.crafts.profileservice.mapper.UserProfileMapper;
import com.crafts.profileservice.repository.impl.UserProfileRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserProfileServiceImplTest {

    private UserProfileServiceImpl userProfileService;

    @Mock
    private UserProfileRepositoryImpl userProfileRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        userProfileService = new UserProfileServiceImpl(userProfileRepository, userProfileMapper);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testUpdateAfterValidation_Success() throws UserProfileBusinessException {
        UserProfileDTO mockDTO = new UserProfileDTO();
        mockDTO.setUserId("user123");
        UserProfileEO mockEO = new UserProfileEO();

        when(userProfileMapper.convertDTOTOEO(mockDTO)).thenReturn(mockEO);
        when(userProfileRepository.update(anyString(), any())).thenReturn(mockEO);
        when(userProfileMapper.convertEOtoDTO(mockEO)).thenReturn(mockDTO);

        UserProfileDTO result = userProfileService.updateAfterValidation(mockDTO);
        assertEquals(mockDTO, result);
    }

    @Test
    public void testUpdateAfterValidation_Exception() {
        UserProfileDTO mockDTO = new UserProfileDTO();
        mockDTO.setUserId("user123");

        when(userProfileMapper.convertDTOTOEO(mockDTO)).thenReturn(new UserProfileEO());
        when(userProfileRepository.update(anyString(), any())).thenThrow(UserProfileRepositoryException.class);
        assertThrows(UserProfileBusinessException.class, () -> {
            userProfileService.updateAfterValidation(mockDTO);
        });
    }

}


