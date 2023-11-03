package com.crafts.profileconsumer.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.crafts.profileconsumer.entity.ProductRegistryEO;
import com.crafts.profileconsumer.exception.UserProfileBusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductRegistryRepositoryImplTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    private ProductRegistryRepositoryImpl productRegistryRepository;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        productRegistryRepository = new ProductRegistryRepositoryImpl(dynamoDBMapper);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testFindAllSuccess() {
        List<ProductRegistryEO> products = productRegistryRepository.findAll();
        assertNull(products);
    }

    @Test
    public void testFindAllEmptyList() {
        PaginatedScanList<ProductRegistryEO> mockScanResult = mock(PaginatedScanList.class);
        when(mockScanResult.isEmpty()).thenReturn(true);
        when(dynamoDBMapper.scan(eq(ProductRegistryEO.class), any(DynamoDBScanExpression.class)))
                .thenReturn(mockScanResult);

        List<ProductRegistryEO> products = productRegistryRepository.findAll();
        assertTrue(products.isEmpty());
    }

    @Test
    public void testFindAllThrowsException() {
        when(dynamoDBMapper.scan(eq(ProductRegistryEO.class), any(DynamoDBScanExpression.class)))
                .thenThrow(new AmazonDynamoDBException("DynamoDB error"));

        assertThrows(UserProfileBusinessException.class, () -> productRegistryRepository.findAll());
    }
}
