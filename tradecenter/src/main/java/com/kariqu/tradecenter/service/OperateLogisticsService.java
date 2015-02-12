package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.LogisticsInfo;

/**
 * 物流信息查询服务
 *
 * @author Athens(刘杰)
 * @Time 2012-09-25 12:54
 * @since 1.0.0
 */
public interface OperateLogisticsService {

    /**
     * 处理第三方物流发过来的数据.
     *
     * @param msg 第三方物流发送过来的物流信息.
     */
    public void receiveThirdLogisticsInfo(String msg);

    /**
     * 向第三方物流发送请求并写入 db, 若之前有发送则勿需再发送.
     *
     * @param company 快递公司
     * @param expressNumber  物流单号
     * @param from    从哪来
     * @param to      到哪去
     */
    void handleThirdLogisticsInfo(String company, String expressNumber, String from, String to);

    /**
     * 查询物流信息.
     *
     * @param expressNumber 物流单号
     */
    LogisticsInfo queryLogistics(String expressNumber);

    /**
     * 添加第三方物流信息.
     */
    void insertLogistics(LogisticsInfo info);

    /**
     * 更新第三方物流信息.
     */
    void updateLogistics(LogisticsInfo info);

}
