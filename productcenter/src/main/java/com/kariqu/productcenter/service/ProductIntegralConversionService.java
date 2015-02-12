package com.kariqu.productcenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductIntegralConversion;

import java.util.Date;
import java.util.List;

/**
 * User: Json.zhu
 * Date: 14-01-02
 * Time: 上午 11:09
 * 积分兑换
 */
public interface ProductIntegralConversionService {
    /**
     * 查询所有的
     * @return
     */
    List<ProductIntegralConversion> queryAllProductIntegralConversion();

    /**
     * 根据主见id查询
     * @return
     */
    ProductIntegralConversion queryProductIntegralConversionById(int id);

    /**
     * 根据SKUID和当前时间查询产品正在做的活动
     * @param skuId
     * @param specificDate
     * @return
     */
    ProductIntegralConversion queryProductIntegralConversionBySkuId(long skuId,Date specificDate);

    /**
     * 分页查询 根据商品id查询
     * @return
     */
    Page<ProductIntegralConversion> queryProductIntegralConversionByProductId(int start, int limit, int productId);

    /**
     * 插入
     * @param productIntegralConversion
     */
    void createProductIntegralConversion(ProductIntegralConversion productIntegralConversion) throws ProductActivityException ;

    /**
     * 插入
     * @param id
     */
    void deleteProductIntegralConversionById(int id);

    /**
     * 更新
     * @param productIntegralConversion
     */
    void updateProductIntegralConversionById(ProductIntegralConversion productIntegralConversion) throws ProductActivityException;


    /**
     * 返回今天的积分兑换信息
     * @return
     */
    List<ProductIntegralConversion> fetchActivityInToday();

}
