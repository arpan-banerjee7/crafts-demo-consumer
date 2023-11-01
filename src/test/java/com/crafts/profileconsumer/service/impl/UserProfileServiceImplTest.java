package com.crafts.profileconsumer.service.impl;

import com.crafts.profileconsumer.dto.UserProfileDTO;
import com.crafts.profileconsumer.entity.UserProfileEO;
import com.crafts.profileconsumer.exception.UserProfileBusinessException;
import com.crafts.profileconsumer.exception.UserProfileRepositoryException;
import com.crafts.profileconsumer.mapper.UserProfileMapper;
import com.crafts.profileconsumer.repository.impl.UserProfileRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

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


