package com.crafts.profileconsumer.service.impl;

import com.crafts.profileconsumer.entity.ProductRegistryEO;
import com.crafts.profileconsumer.exception.UserProfileBusinessException;
import com.crafts.profileconsumer.repository.ProductRegistryRepository;
import com.crafts.profileconsumer.service.ProductRegistryService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProductRegistryServiceImpl implements ProductRegistryService {

    @Autowired
    private ProductRegistryRepository productRegistryRepository;
    private Map<String, String> productValidationEndpoints;

    @PostConstruct
    public void loadDataOnStartup() {
        try {
            List<ProductRegistryEO> productRegistryData = productRegistryRepository.findAll();

            // Build a map of productId to productValidationUrl
            productValidationEndpoints = new HashMap<>();
            for (ProductRegistryEO product : productRegistryData) {
                productValidationEndpoints.put(product.getProductId(), product.getProductValidationUrl());
            }
        } catch (UserProfileBusinessException e) {
            throw new UserProfileBusinessException("Failed to Load Product Validation Endpoints", e);
        }

    }

    @Override
    public String getProductValidationEndpoint(String productId) {
        if (productValidationEndpoints == null) {
            throw new UserProfileBusinessException("Product data is not present");
        }
        return productValidationEndpoints.get(productId);
    }

}

