package com.kariqu.tradecenter.repository;

import com.kariqu.tradecenter.domain.BackGoodsLog;
import com.kariqu.tradecenter.domain.BackGoodsState;

import java.util.List;

public interface BackGoodsLogRepository {

    /**
     * 增加退货日志信息
     *
     * @param record
     */
	void insert(BackGoodsLog record);

    /**
     * 动态更新
     *
     * @param record
     * @return
     */
    int update(BackGoodsLog record);

    /**
     * 查询指定退货单的所有日志信息
     *
     * @param backId
     * @return
     */
    List<BackGoodsLog> selectByBackId(Long backId);

    /**
     * 查询指定退货单的 指定退货单状态 最新的日志信息
     *
     * @param backId 退货单编号
     * @param backState 需要查询的退货单状态
     * @return
     */
    BackGoodsLog selectByState(Long backId, BackGoodsState backState);

    /**
     * 查询所有指定退货单编号中按时间倒序后最新的那条记录.
     *
     *
     * @param backGoodsIdList
     * @return
     */
    BackGoodsLog selectRecentLogByOrderId(Long backGoodsIdList);

    /**
     * 删除指定退货单下的所有日志信息.
     *
     * @param backId
     * @return
     */
    int deleteByBackId(Long backId);

}