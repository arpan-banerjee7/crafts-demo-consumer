package com.crafts.profileconsumer.repository;

import com.crafts.profileconsumer.entity.UserProfileEO;

public interface UserProfileRepository {
    UserProfileEO update(String userId, UserProfileEO userProfile);

}
