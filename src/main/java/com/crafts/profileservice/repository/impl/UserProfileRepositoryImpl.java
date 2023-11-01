package com.crafts.profileservice.repository.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.crafts.profileservice.entity.UserProfileEO;
import com.crafts.profileservice.exception.UserProfileRepositoryException;
import com.crafts.profileservice.repository.UserProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

@Repository
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public UserProfileRepositoryImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public UserProfileEO update(String userId, UserProfileEO userProfile) {
        try {
              /* Save user profile based on certain conditions
               Here, We are checking if the user_id is matching with the userId of the UserProfile object
             */
            DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression().withExpectedEntry("userId",
                    new ExpectedAttributeValue(new AttributeValue().withS(userId)));
            dynamoDBMapper.save(userProfile, saveExpression, DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES.config());
            return userProfile;
        } catch (ConditionalCheckFailedException e) {
            throw new NoSuchElementException("User profile ID mismatch or condition check failed", e);
        } catch (AmazonDynamoDBException e) {
            throw new UserProfileRepositoryException("Failed to update user profile due to DynamoDB error", e);
        } catch (Exception e) {
            throw new UserProfileRepositoryException("An unexpected error occurred while updating the user profile", e);
        }
    }

}
