package com.crafts.profileservice.service.impl;

import com.crafts.profileservice.constans.ProfileServiceCache;
import com.crafts.profileservice.dto.UserProfileDTO;
import com.crafts.profileservice.entity.UserProfileEO;
import com.crafts.profileservice.enums.ValidationStatusEnum;
import com.crafts.profileservice.exception.UserProfileBusinessException;
import com.crafts.profileservice.exception.UserProfileRepositoryException;
import com.crafts.profileservice.mapper.UserProfileMapper;
import com.crafts.profileservice.repository.impl.UserProfileRepositoryImpl;
import com.crafts.profileservice.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepositoryImpl userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileServiceImpl(UserProfileRepositoryImpl userProfileRepository, UserProfileMapper userProfileMapper) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }


    @Override
    @CacheEvict(value = ProfileServiceCache.USER_PROFILE_CACHE, key = "#userProfileDTO.userId", condition = "#userProfileDTO != null and #userProfileDTO.userId != null")
    public UserProfileDTO updateAfterValidation(UserProfileDTO userProfileDTO) throws UserProfileBusinessException {
        try {
            UserProfileEO userProfileEO = userProfileMapper.convertDTOTOEO(userProfileDTO);
            log.info("Updating user {} with user details {}", userProfileDTO.getUserId(), userProfileDTO.toString());
            userProfileRepository.update(userProfileDTO.getUserId(), userProfileEO);
            return userProfileMapper.convertEOtoDTO(userProfileEO);
        } catch (UserProfileRepositoryException e) {
            log.info("Failed to update user data {}", userProfileDTO.getUserId());
            throw new UserProfileBusinessException("Error while updating user profile after validation", e);
        }
    }

    private void handleRollback(UserProfileDTO userProfileDTO) {
        log.error("Could not send message to kafka for carrying out validations, logging event as not complete in DB");
        UserProfileDTO failedUserProfileDTO = new UserProfileDTO();
        failedUserProfileDTO.setUserId(userProfileDTO.getUserId());
        failedUserProfileDTO.setConsolidatedStatus(ValidationStatusEnum.NOT_COMPLETE.getStatus());
        failedUserProfileDTO.setConsolidatedMessage("Could not perform profile validation due to some unexpected error from a subscribed product.");
        UserProfileEO failedUserProfileEO = userProfileMapper.convertDTOTOEO(failedUserProfileDTO);
        userProfileRepository.update(userProfileDTO.getUserId(), failedUserProfileEO);
        log.error("Rolled back status from IN_PROGRESS to NOT_COMPLETE due to kafka server error");
    }

}

