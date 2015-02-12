package com.kariqu.tradecenter.repository;

import com.kariqu.tradecenter.domain.LogisticsInfo;

/**
 * @author Athens(刘杰)
 * @Time 2012-09-25 15:25
 * @since 1.0.0
 */
public interface OperaLogisticsRepository {

    /**
     * 查询物流
     *
     * @param number 物流单号
     * @return
     */
    LogisticsInfo select(String number);

    /**
     * 生成物流信息
     *
     * @param info
     */
    void insert(LogisticsInfo info);

    /**
     * 更新物流信息
     *
     * @param info
     */
    void update(LogisticsInfo info);

    /**
     * 删除, 暂时没用到.
     *
     * @param number 物流单号
     */
    void delete(String number);

}
