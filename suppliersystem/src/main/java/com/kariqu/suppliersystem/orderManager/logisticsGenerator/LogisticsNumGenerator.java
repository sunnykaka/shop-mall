package com.kariqu.suppliersystem.orderManager.logisticsGenerator;

import java.util.List;

/**
 *
 * 物流单号的生成接口
 * User: amos.zhou
 * Date: 13-9-28
 * Time: 下午2:58
 *
 */
public interface LogisticsNumGenerator {

    /**
     *生成物流单号
     * @param initNo 生成的起始单号
     * @param law    递增数
     * @param count  生成数量
     * @return
     */
    public List<String> generateNumList(String initNo, int law, int count) throws GenerateException;
}
