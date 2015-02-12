package com.kariqu.productcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductIntegralConversion;

import java.util.Date;
import java.util.List;

/**
 * User: Json.zhu
 * Date: 13-12-31
 * Time: 下午01:39
 * 积分兑换
 */
public interface ProductIntegralConversionRepository {
    List<ProductIntegralConversion> selectAllProductIntegralConversionBySkuId(int skuId);

    ProductIntegralConversion fetchIntegralConversionBySkuIdAndDaytime(int skuId, int productId, Date specificDate);

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
     * 查询某个商品在某个特定时间点的活动，如果没有则返回 null.
     * 在同一时间只能有一个活动
     * @param skuId
     * @param specificDate
     * @return
     */
    ProductIntegralConversion getProductIntegralConversionByGivingTime(long skuId , Date specificDate);

    /**
     * 分页查询 根据商品id查询
     * @return
     */
    Page<ProductIntegralConversion> queryProductIntegralConversionByProductId(int start, int limit, int productId);

    /**
     * 查找指定日期 积分换购的活动
     * @param curDate
     * @return
     */
    List<ProductIntegralConversion> selectProductIntegralConversionByDate(Date curDate);

    /**
     * 插入
     * @param productIntegralConversion
     */
    void createProductIntegralConversion(ProductIntegralConversion productIntegralConversion);

    /**
     * 插入
     * @param id
     */
    void deleteProductIntegralConversionById(int id);

    /**
     * 更新
     * @param productIntegralConversion
     */
    void updateProductIntegralConversionById(ProductIntegralConversion productIntegralConversion);

}
