package com.kariqu.tradecenter.repository;

import com.kariqu.tradecenter.domain.payment.SkuTradeResult;

/**
 * @author Athens(刘杰)
 * @Time 2012-11-16 17:30
 * @since 1.0.0
 */
public interface SkuTradeResultRepository {

    /**
     * 添加 sku 的销售数量.
     *
     * @param result
     */
    void insert(SkuTradeResult result);

    /**
     * 更新 sku 的销售数量.
     *
     * @param result
     */
    void update(SkuTradeResult result);

    /**
     * 依主键获取.
     *
     * @param id
     * @return
     */
    SkuTradeResult getById(long id);

    /**
     * 通过 sku 获取数据.
     *
     * @param skuId sku
     * @return
     */
    SkuTradeResult getBySkuId(long skuId);

    /**
     * 通过 商品编号 获取销售数量.
     *
     * @param productId
     * @return
     */
    SkuTradeResult getByProductId(int productId);

    /**
     * 删除.
     *
     * @param id
     */
    void delete(long id);

}
