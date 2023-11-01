package com.crafts.profileconsumer.service;


import com.crafts.profileconsumer.dto.UserProfileDTO;
import com.crafts.profileconsumer.exception.UserProfileBusinessException;

public interface UserProfileValidationService {
    void validateUserProfile(String message) throws UserProfileBusinessException;
    void validateUserProfile(UserProfileDTO userProfileDTO) throws UserProfileBusinessException;
}
