package com.crafts.profileconsumer.repository;

import com.crafts.profileconsumer.entity.ProductRegistryEO;

import java.util.List;

public interface ProductRegistryRepository {
    List<ProductRegistryEO> findAll();

}

