package com.kariqu.tradecenter.repository;

import com.kariqu.tradecenter.domain.BackGoodsItem;

import java.util.List;

public interface BackGoodsItemRepository {

    /**
     * 添加退货详情信息
     *
     * @param record
     */
    void insert(BackGoodsItem record);

    /**
     * 更新退货详情信息
     *
     * @param record
     * @return
     */
    int updateBackGoodsItem(BackGoodsItem record);

    /**
     * 查询指定退货单的详情信息
     *
     * @param backId 退货单编号
     * @return
     */
    List<BackGoodsItem> selectByBackGoodsId(Long backId);

    /**
     * 查询指定订单项编号对应的退货项数据(若一次订单项只能退货一次, 则只应该有一条数据, 若允许有多次, 则应累积数量)
     *
     * @param backGoodsId 退货单编号集(退货单有可能会被取消过, 要先查出未被取消未被删除的退货单)
     * @param orderItemId 订单项编号
     * @return
     */
    int selectByBackOrderItemId(Long backGoodsId, Long orderItemId);

    /**
     * 删除指定退货单下的详情信息
     *
     * @param backId 退货单编号
     * @return
     */
    int deleteByBackGoodsId(Long backId);

}
