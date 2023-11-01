package com.crafts.profileservice.service;

import com.crafts.profileservice.dto.UserProfileDTO;

public interface UserProfileService {

    UserProfileDTO updateAfterValidation(UserProfileDTO userProfile);

}
