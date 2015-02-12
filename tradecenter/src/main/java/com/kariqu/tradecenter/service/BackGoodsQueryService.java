package com.kariqu.tradecenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.BackGoodsNoTransactionalException;

import java.util.List;

/**
 * 订单查询服务
 *
 * @author Tiger
 * @version 1.0
 * @since 13-1-10 下午4:13
 */
public interface BackGoodsQueryService {

    /**
     * 查询某个订单下可以退货的数据.
     *
     * @param orderNo 订单编号
     * @param userId
     * @return
     * @throws BackGoodsNoTransactionalException
     */
    Result<OrderItem, Order> queryCanBackOrderItemsOfUser(long orderNo, int userId) throws BackGoodsNoTransactionalException;

    /**
     * 查询退货单进度.
     *
     * @param backGoodsId 退货单编号
     * @return
     */
    Progress queryBackProgress(long backGoodsId);

    /**
     * 查询指定用户的所有退货单.
     *
     * @param userId 用户编号
     * @return
     */
    List<BackGoods> queryBackGoodsByUserId(int userId);

    /**
     * 查询指定订单的所有退货单.
     *
     * @param orderNo 订单编号
     * @param userId  用户编号
     * @return
     */
    List<BackGoods> queryBackGoodsByOrderNoAndUserId(long orderNo, int userId);

    /**
     * 查询指定订单的所有(未删除、未取消)的退货单Id.
     *
     * @param orderNo 订单编号
     * @param userId  用户编号
     * @return
     */
    List<Long> queryBackGoodsIdByOrderNoAndUserId(long orderNo, int userId);

    /**
     * 查询订单Id对应的所有未删除且未取消且已审核通过的退货单数据.
     *
     * @param orderId 订单编号
     * @return
     */
    List<BackGoods> queryBackGoodsByOrderId(long orderId);

    /**
     * 查询指定用户的所有退货单信息. 分页
     *
     * @param userId 用户Id
     * @param page
     * @return
     */
    Page<BackGoods> queryBackGoodsByUserIdForPage(int userId, Page<BackGoods> page);

    /**
     * 查询退货单数据.
     *
     * @param backGoodsId 退货单编号
     * @return
     */
    BackGoods queryBackGoodsById(long backGoodsId);

    /**
     * 查询指定用户的单条退货单数据.
     *
     *
     * @param userId      用户Id
     * @param backGoodsId 退货单编号
     * @return
     */
    BackGoods queryBackGoodsAndItemByIdAndUserId(int userId, long backGoodsId);

    /**
     * 查询单个状态未删除的所有退货单.
     *
     * @param state
     * @return
     */
    List<BackGoods> queryBackGoodsByState(BackGoodsState state);

    /**
     * 查询所有需要财务退款的退货单数据.
     *
     * @return
     */
    List<BackGoods> queryBackGoodsForFinance();

    /**
     * 查询退货单下的所有退货详情.
     *
     * @param backGoodsId 退货单编号
     * @return
     */
    List<BackGoodsItem> queryBackGoodsItemByBackGoodsId(long backGoodsId);

    /**
     * 查询指定订单项编号对应的退货项数量(若一次订单项只能退货一次, 则只应该有一条数据, 若允许有多次, 则应累积数量)
     *
     * @param backGoodsId 退货单编号
     * @param orderItemId 订单项编号
     * @return
     */
    int queryByBackOrderItemId(Long backGoodsId, Long orderItemId);

    /**
     * 查询退货单下所有用户可见的记录信息.
     *
     * @param backGoodsId 退货单编号
     * @return
     */
    List<BackGoodsLog> queryBackGoodsLogByBackGoodsIdForUser(long backGoodsId);

    /**
     * 查询指定退货单的 指定退货单状态 最新的日志信息
     *
     * @param backGoodsId 退货单编号
     * @param backState   需要查询的退货单状态
     * @return
     */
    BackGoodsLog queryBackGoodsLogByState(long backGoodsId, BackGoodsState backState);

    /**
     * 查询所有指定订单对应的所有已经通过审核的退货单编号中按时间倒序后最新的那条记录.
     *
     * @param orderId
     * @return
     */
    public BackGoodsLog queryRecentBackGoodsLogByOrderId(long orderId);

    /**
     * 查询所有等待客服处理的退货单数量
     *
     * @return
     */
    int queryBackGoodsCountForWaitingToAudit();

    /**
     * 查询所有等待财务退款的退货单数量
     *
     * @return
     */
    int queryBackGoodsCountForFinance();

}
