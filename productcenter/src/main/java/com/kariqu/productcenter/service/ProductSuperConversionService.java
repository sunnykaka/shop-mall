package com.kariqu.productcenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductSuperConversion;

import java.util.Date;
import java.util.List;

/**
 * User: Json.zhu
 * Date: 14-01-02
 * Time: 上午 11:09
 * 商品超值兑换表
 */
public interface ProductSuperConversionService {
    /**
     * 查询所有的
     * @return
     */
    List<ProductSuperConversion> queryAllProductSuperConversion();

    /**
     * 根据主见id查询
     * @return
     */
    ProductSuperConversion queryProductSuperConversionById(int id);

    /**
     * 根据SKUID和当前时间查询产品正在做的活动
     * @param skuId
     * @param date
     * @return
     */
    ProductSuperConversion queryProductSuperConversionBySkuId(long skuId,Date date);

    /**
     * 分页查询 根据商品id查询
     * @return
     */
    Page<ProductSuperConversion> queryProductSuperConversionByProductId(int start, int limit, int productId);

    /**
     * 插入
     * @param productSuperConversion
     */
    void createProductSuperConversion(ProductSuperConversion productSuperConversion) throws ProductActivityException;

    /**
     * 插入
     * @param id
     */
    void deleteProductSuperConversionById(int id);

    /**
     * 更新
     * @param productSuperConversion
     */
    void updateProductSuperConversionById(ProductSuperConversion productSuperConversion) throws ProductActivityException;


    List<ProductSuperConversion> fetchActivityInToday();

}
