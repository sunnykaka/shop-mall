package com.kariqu.suppliersystem.orderManager.logisticsGenerator;

import com.kariqu.suppliercenter.domain.DeliveryInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * User: amos.zhou
 * Date: 13-9-28
 * Time: 下午3:42
 */
public class LogisticsGeneratorFactory {

    private LogisticsGeneratorFactory() {
    }

    private static final Map<String, LogisticsNumGenerator> logisticsMap = new HashMap<String, LogisticsNumGenerator>();


    static {
        logisticsMap.put(DeliveryInfo.DeliveryType.shunfeng.toDesc(), new SFLogisticsNumGenerator());
        logisticsMap.put(DeliveryInfo.DeliveryType.ems.toDesc(), new SFLogisticsNumGenerator());
    }


    /**
     * 根据配送方式的拼音获取物流单号生成器
     * @param logisticsType
     * @return
     */
    public static LogisticsNumGenerator getGenerator(String logisticsType) {
        return logisticsMap.get(logisticsType) == null ? new AutoIncreaseLogisticsNumGenerator() : logisticsMap.get(logisticsType);
    }


    /**
     * 根据配送方式获取物流单号生成器
     * @param logisticsType
     * @return
     */
    public static LogisticsNumGenerator getGenerator(DeliveryInfo.DeliveryType logisticsType) {
        return getGenerator(logisticsType.toDesc());
    }


}
