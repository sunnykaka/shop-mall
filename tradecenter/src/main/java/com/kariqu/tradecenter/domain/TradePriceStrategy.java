package com.kariqu.tradecenter.domain;

/**
 * User: Asion
 * Date: 13-5-30
 * Time: 下午6:10
 */
public interface TradePriceStrategy {

    void apply(OrderItem orderItem);
}
