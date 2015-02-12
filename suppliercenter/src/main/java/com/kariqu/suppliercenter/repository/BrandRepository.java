package com.kariqu.suppliercenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.Brand;

import java.util.List;

/**
 * 商品品牌仓库类
 * @author:Wendy
 * @since:1.0.0 Date: 13-1-23
 * Time: 上午11:12
 */
public interface BrandRepository {

    void createBrand(Brand brand);

    void updateBrand(Brand brand);

    void deleteBrandById(int id);

    void deleteBrandByCustomerId(int customerId);


    Brand queryBrandById(int id);

    Brand queryBrandByName(String name);

    List<Brand> queryBrandByCustomerId(int customerId);

    /**
     * 分页， 查询某个商家的品牌
     * @param page
     * @return Page<Brand>
     */
    Page<Brand> queryBrandByPage(int customerId, Page<Brand> page);

    List<Brand> queryAllBrand();

}
