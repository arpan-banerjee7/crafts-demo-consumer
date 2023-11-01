package com.crafts.profileconsumer.service;

import com.crafts.profileconsumer.dto.UserProfileDTO;

public interface UserProfileService {

    UserProfileDTO updateAfterValidation(UserProfileDTO userProfile);

}
