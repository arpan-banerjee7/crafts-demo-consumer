package com.crafts.profileconsumer.mapper;

import com.crafts.profileconsumer.dto.UserProfileDTO;
import com.crafts.profileconsumer.entity.UserProfileEO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserProfileMapper {
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "existingSubscriptions", ignore = true)
    UserProfileDTO convertEOtoDTO(UserProfileEO userProfile);

    @Mapping(target = "consolidatedStatus", expression = "java(userProfile.getConsolidatedStatus() != null ? userProfile.getConsolidatedStatus() : String.valueOf(com.crafts.profileconsumer.enums.ValidationStatusEnum.IN_PROGRESS))")
    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now().toString())")
    UserProfileEO convertDTOTOEO(UserProfileDTO userProfile);

}
