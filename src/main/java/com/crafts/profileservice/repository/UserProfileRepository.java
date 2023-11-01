package com.crafts.profileservice.repository;

import com.crafts.profileservice.entity.UserProfileEO;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Map;

public interface UserProfileRepository {
    UserProfileEO update(String userId, UserProfileEO userProfile);

}
