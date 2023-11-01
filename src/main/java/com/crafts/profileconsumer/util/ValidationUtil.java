package com.crafts.profileconsumer.util;

import com.crafts.profileconsumer.dto.ProductValidationStatus;
import com.crafts.profileconsumer.dto.UserProfileValidationResponseDTO;
import com.crafts.profileconsumer.enums.ValidationStatusEnum;
import com.crafts.profileconsumer.exception.JsonSerializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
public final class ValidationUtil {
    public static UserProfileValidationResponseDTO handleWebClientError(Throwable e) {
        if (e instanceof WebClientResponseException webException) {
            if (webException.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.warn("Validation error: {}, {}", webException.getStatusCode(), webException.getResponseBodyAsString());

                try {
                    // Parse the error response to DTO
                    return JsonUtil.readValue(webException.getResponseBodyAsString(), UserProfileValidationResponseDTO.class);
                } catch (JsonSerializationException jsonEx) {
                    log.error("Failed to parse error response: {}", webException.getResponseBodyAsString());
                    throw new RuntimeException("Error during validation response parsing.", jsonEx);
                }
            }

            // For any other WebClientResponseException that is not a 400, log and rethrow the exception.
            log.error("WebClient Response Error: {}, {}", webException.getStatusCode(), webException.getResponseBodyAsString());
            throw new RuntimeException("Error during validation: " + webException.getMessage(), webException);
        }
        // For all other errors, log and rethrow the exception.
        log.error("Error occurred: {}", e.getMessage());
        throw new RuntimeException("Unexpected error during validation: " + e.getMessage(), e);
    }

    public static ProductValidationStatus getProductValidationStatus(UserProfileValidationResponseDTO validationResponse) {
        ProductValidationStatus productStatus = new ProductValidationStatus();

        if (validationResponse.getErrors() == null || validationResponse.getErrors().isEmpty()) {
            productStatus.setStatus(ValidationStatusEnum.SUCCESS.getStatus());
        } else {
            productStatus.setStatus(ValidationStatusEnum.REJECTED.getStatus());
            productStatus.setErrors(validationResponse.getErrors());
        }
        return productStatus;
    }

}
