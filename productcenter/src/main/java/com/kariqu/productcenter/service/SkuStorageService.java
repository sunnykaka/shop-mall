package com.kariqu.productcenter.service;

import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.suppliercenter.domain.ProductStorage;

/**
 * SKU库存服务
 * User: Asion
 * Date: 12-6-26
 * Time: 下午12:44
 */
public interface SkuStorageService {


    /**
     * 根据skuId和给予的物流信息得到sku所在的库存
     *
     * @param skuId
     * @param location
     * @return
     */
    ProductStorage getConcretionStorage(long skuId, String location);


    /**
     * 通过一个skuId读取它的库存信息，如果sku分仓这个方法无效
     * <p/>
     * TODO 如果以后一个sku在多个仓库，则这个方法要去掉，改用上面的方法
     *
     * @param skuId
     * @return
     */
    ProductStorage getConcretionStorage(long skuId);




    SkuStorage getSkuStorage(long skuId);
}
