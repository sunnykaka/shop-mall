package com.kariqu.productcenter.repository;

import com.kariqu.productcenter.domain.ProductOfPlatform;

/**
 * User: Alec
 * Date: 13-10-17
 * Time: 上午10:00
 */
public interface ProductOfPlatformRepository {

    void addProductToPlatform(ProductOfPlatform product);

    ProductOfPlatform queryProductOfPlatform(int productId, ProductOfPlatform.Platform platform);
}
