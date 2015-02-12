package com.kariqu.productcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductSuperConversion;

import java.util.Date;
import java.util.List;

/**
 * User: Json.zhu
 * Date: 13-12-31
 * Time: 上午 10:09
 * 商品超值兑换表
 */
public interface ProductSuperConversionRepository {
    List<ProductSuperConversion> selectAllProductSuperConversionBySkuId(int skuId);

    ProductSuperConversion fetchSuperConversionBySkuIdAndDaytime(int skuId, int productId, Date specificDate);

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
     * 查询某个商品在某个特定时间点的活动，如果没有则返回 null.
     * 在同一时间只能有一个活动
     * @param skuId
     * @param specificDate
     * @return
     */
    ProductSuperConversion getProductSuperConversionByGivingTime(long skuId, Date specificDate);

    /**
     * 分页查询 根据商品id查询
     * @return
     */
    Page<ProductSuperConversion> queryProductSuperConversionByProductId(int start, int limit, int productId);

    /**
     * 查找指定产品id及日期查找 积分优惠购的活动
     * @param curDate
     * @return
     */
    List<ProductSuperConversion> selectSuperConversionByDate(Date curDate);

    /**
     * 插入
     * @param productSuperConversion
     */
    void createProductSuperConversion(ProductSuperConversion productSuperConversion);

    /**
     * 插入
     * @param id
     */
    void deleteProductSuperConversionById(int id);

    /**
     * 更新
     * @param productSuperConversion
     */
    void updateProductSuperConversionById(ProductSuperConversion productSuperConversion);

}
