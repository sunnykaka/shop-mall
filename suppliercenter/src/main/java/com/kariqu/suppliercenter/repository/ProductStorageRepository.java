package com.kariqu.suppliercenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.ProductStorage;

import java.util.List;

/**
 * 商品库存仓库类
 * @author:Wendy
 * @since:1.0.0 Date: 13-1-23
 * Time: 上午11:12
 */
public interface ProductStorageRepository {

    void createProductStorage(ProductStorage productStorage);

    void updateProductStorage(ProductStorage productStorage);

    ProductStorage queryProductStorageById(int id);

    ProductStorage queryProductStorageByNameAndCustomerId(String name, int customerId);

    void deleteProductStorageById(int id);

    void deleteProductStorageByCustomerId(int customerId);


    List<ProductStorage> queryProductStorageByCustomerId(int customerId);

    /**
     * 分页
     * @param page
     * @return Page<ProductStorage>
     */
    Page<ProductStorage> queryProductStorageByPage(int customerId, Page<ProductStorage> page);

    int queryProductStorageNumber(int customerId);

    int querySkuCountByProductStorageId(int productStorageId);
}
