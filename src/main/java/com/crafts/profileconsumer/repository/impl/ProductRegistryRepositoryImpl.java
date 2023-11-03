package com.crafts.profileconsumer.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.crafts.profileconsumer.entity.ProductRegistryEO;
import com.crafts.profileconsumer.exception.UserProfileBusinessException;
import com.crafts.profileconsumer.repository.ProductRegistryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRegistryRepositoryImpl implements ProductRegistryRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public ProductRegistryRepositoryImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    @Override
    public List<ProductRegistryEO> findAll() {
        try {
            return dynamoDBMapper.scan(ProductRegistryEO.class, new DynamoDBScanExpression());
        } catch (Exception e) {
            throw new UserProfileBusinessException("Failed to load product details", e);
        }
    }

}