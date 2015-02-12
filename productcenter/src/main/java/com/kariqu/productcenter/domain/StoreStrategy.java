package com.kariqu.productcenter.domain;

import com.kariqu.productcenter.service.SkuService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Athens(刘杰)
 * @Time 2012-08-31 18:05
 * @since 1.0.0
 */
public enum StoreStrategy {

    /** 普通策略(创建则扣减库存,取消则回加库存.) */
    NormalStrategy {
        @Override
        public void operateStorageWhenCreateOrder(SkuService skuService, long skuId,
                        long productStorageId, int number, boolean isCashOnDelivery) {
            skuService.minusSkuStock(skuId, productStorageId, number);
        }

        @Override
        public void operateStorageWhenCancelOrder(SkuService skuService, long skuId,
                                  long productStorageId, int number, boolean isCashOnDelivery) {
            skuService.addSkuStock(skuId, productStorageId, number);
        }

        @Override
        public void operateStorageWhenPayOrder(SkuService skuService, long skuId, long productStorageId, int number) {
        }
    },

    /** 支付成功则扣减库存策略 */
    PayStrategy {
        @Override
        public void operateStorageWhenCreateOrder(SkuService skuService, long skuId,
                                     long productStorageId, int number, boolean isCashOnDelivery) {
            if (isCashOnDelivery) {
                skuService.minusSkuStock(skuId, productStorageId, number);
            }
        }

        @Override
        public void operateStorageWhenCancelOrder(SkuService skuService, long skuId,
                                     long productStorageId, int number, boolean isCashOnDelivery) {
            if (isCashOnDelivery) {
                skuService.addSkuStock(skuId, productStorageId, number);
            }
        }

        @Override
        public void operateStorageWhenPayOrder(SkuService skuService, long skuId, long productStorageId, int number) {
            skuService.minusSkuStock(skuId, productStorageId, number);
        }
    };

    /**
     * 创建订单时的库存操作(如果是货到付款, 创建订单时减库存, 不管哪种策略)
     *
     * @param skuService
     * @param skuId sku编号
     * @param productStorageId 仓库编号
     * @param number 购买数量
     * @param isCashOnDelivery 是否是货到付款
     */
    public abstract void operateStorageWhenCreateOrder(SkuService skuService, long skuId,
                             long productStorageId, int number, boolean isCashOnDelivery);

    /**
     * 取消订单时的库存操作(如果是货到付款, 取消时回加库存, 不管哪种策略)
     *
     * @param skuService
     * @param skuId sku编号
     * @param productStorageId 仓库编号
     * @param number 购买数量
     * @param isCashOnDelivery 是否是货到付款
     */
    public abstract void operateStorageWhenCancelOrder(SkuService skuService, long skuId,
                                        long productStorageId, int number, boolean isCashOnDelivery);

    /**
     * 订单支付成功后的库存操作
     *
     * @param skuService
     * @param skuId sku编号
     * @param productStorageId 仓库编号
     * @param number 购买数量
     */
    public abstract void operateStorageWhenPayOrder(SkuService skuService, long skuId, long productStorageId, int number);

    private static Map<StoreStrategy, String> mapping = new HashMap<StoreStrategy, String>();

    static {
        mapping.put(NormalStrategy, "普通策略");
        mapping.put(PayStrategy, "支付策略");
    }

    public String toDesc() {
        return mapping.get(this);
    }

    public boolean isPayStrategy() {
        return this == PayStrategy;
    }
}
