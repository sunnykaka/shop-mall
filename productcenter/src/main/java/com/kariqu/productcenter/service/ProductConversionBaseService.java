package com.kariqu.productcenter.service;

import com.kariqu.common.lib.F;
import com.kariqu.productcenter.domain.ProductConversionBase;

import java.util.Date;

/**
 * 积分兑换,换购公共服务, 主要是把共同的逻辑提取出来
 * Created by Canal.wen on 2014/8/5 12:03.
 */
public interface ProductConversionBaseService {

    /**
     * 按skuid及时间获取商品sku参加的活动. 如果都没有则返回null
     * @param productId
     * @param skuId
     * @return
     */
    ProductConversionBase fetchConversionBySkuIdAndDaytime(int skuId, int productId, Date specialDate);

    /**
     * 按skuid 检查活动的sdate, edate是否可参加活动.
     * @return F.T2  _1 boolean 是否可插入, _2 string 不能插入的原因
     */
    F.T2<Boolean,String> checkConversionAssign(ProductConversionBase productConversionBase);

    /**
     * 按skuid 检查活动的sdate, edate是否可修改活动, 也就是不包含自己了
     * @return F.T2  _1 boolean 是否可插入, _2 string 不能插入的原因
     */
    F.T2<Boolean,String> checkConversionAssignWithoutSelf(ProductConversionBase productConversionBase);
}
