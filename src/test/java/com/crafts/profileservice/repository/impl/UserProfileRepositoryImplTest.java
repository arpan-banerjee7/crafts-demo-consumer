package com.crafts.profileservice.repository.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.crafts.profileservice.entity.UserProfileEO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

public class UserProfileRepositoryImplTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private AmazonDynamoDB dynamoDBClient;

    private UserProfileRepositoryImpl userProfileRepository;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        userProfileRepository = new UserProfileRepositoryImpl(dynamoDBMapper);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testUpdateUserProfile() {
        // No need to stub the dynamoDBMapper.save method when it's a successful update.
        userProfileRepository.update("testId", new UserProfileEO());

        doThrow(new ConditionalCheckFailedException("Condition failed"))
                .when(dynamoDBMapper)
                .save(any(UserProfileEO.class), any(DynamoDBSaveExpression.class), any(DynamoDBMapperConfig.class));
        assertThrows(NoSuchElementException.class, () -> userProfileRepository.update("testId", new UserProfileEO()));
    }
}
