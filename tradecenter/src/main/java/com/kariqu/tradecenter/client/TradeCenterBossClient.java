package com.kariqu.tradecenter.client;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.BackGoodsBaseException;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.service.OrderQuery;
import com.kariqu.usercenter.domain.User;

import java.util.Date;
import java.util.List;

/**
 * 交易中心客服以及运营客户端
 *
 * @author Tiger
 * @version 1.0
 * @since 13-1-25 上午10:09
 */
public interface TradeCenterBossClient {

    void updateLogisticsRedundancy(LogisticsRedundancy logisticsRedundancy);

    /**
     * 更新订单的发票信息
     *
     * @param orderId
     * @param invoiceInfo
     */
    void updateOrderInvoiceInfoRedundancy(long orderId, InvoiceInfo invoiceInfo);

    /**
     * 更新订单的物流快递公司
     *
     * @param orderId
     * @param deliveryType
     */
    void updateOrderDeliveryType(long orderId, DeliveryInfo.DeliveryType deliveryType);

    /**
     * 更新订单的物流编号
     * @param orderId
     * @param waybillNumber
     */
    void updateWaybillNumberByOrderId(long orderId, String waybillNumber);

    /**
     * 客服添加订单留言信息(每次都是新加)
     *
     * @param orderMessage
     */
    void appendOrderMessage(OrderMessage orderMessage);

    /**
     * 为用户添加积分及现金券
     *
     * @param user
     * @param point
     * @param couponInfo
     */
    void assignIntegralAndCoupon(User user, long point, String couponInfo);

    /**
     * 客服审核已经付款成功的订单
     *
     * @param orderId
     * @param operator 操作人(写入历史记录)
     */
    void approvalOrderOfPaySuccess(long orderId, String operator) throws OrderBaseException;

    /**
     * 查询订单项的实际发货数量(把已经退货的订单项数量去除).
     *
     * @param orderId
     * @return
     */
    List<OrderItem> queryOrderItemWithoutBackingNumberByOrderId(long orderId);

    /**
     * 查询所有等待客服审核的订单数量
     *
     * @return
     */
    int queryCountOfOrderWaitForApproval();


    /**
     * 查询订单
     *
     * @param orderQuery
     * @return
     */
    Page<Order> searchOrderByQuery(OrderQuery orderQuery);

    /**
     * 查询订单, 无分页信息, 主要用来订单导出
     *
     *
     * @param orderQuery
     * @return
     */
    List<List<Order>> searchOrderListByQuery(OrderQuery orderQuery);

    /**
     * 查询单个订单某个状态的单条最新历史记录时间
     *
     *
     * @param orderId
     * @return
     */
    Date querySuccessDate(long orderId);

    /**
     * 查询订单的物流信息
     *
     * @param orderId
     * @return
     */
    Logistics queryLogisticsByOrderId(long orderId);


    /**
     * 查询冗余的订单物流信息
     *
     * @param logisticsId
     * @return
     */
    LogisticsRedundancy queryLogisticsRedundancy(long logisticsId);


    /**
     * 根据订单id查询订单(包含了订单项, 物流信息, 发票信息)
     */
    Order queryOrderById(long orderId);

    /**
     * 查询订单信息
     *
     * @param orderNo
     * @return
     */
    Order queryOrderByOrderNo(long orderNo);

    /**
     * 查询订单发票信息
     *
     * @param orderId
     * @return
     */
    InvoiceInfo queryOrderInvoiceInfoRedundancy(long orderId);

    /**
     * 查询订单的所有留言信息.
     *
     * @param orderId
     * @return
     */
    List<OrderMessage> queryAllMessage(long orderId);

    /**
     * 查询用户的最新留言信息.
     *
     * @param orderId
     * @return
     */
    OrderMessage queryUserMessage(long orderId);

    /**
     * 查询客服的最新留言信息.
     *
     * @param orderId
     * @return
     */
    OrderMessage queryServerMessage(long orderId);

    /**
     * 查询订单的状态历史记录(若有物流信息也返回)
     *
     * @param orderId
     * @return
     */
    List<ProgressDetail> getProgressDetail(long orderId);


    /**
     * 查询指定状态的退货单
     *
     * @param backState
     * @return
     */
    List<BackGoods> queryBackGoodsWithBackGoodsState(BackGoodsState backState);

    /**
     * 根据退单id查询退单项详情
     *
     * @param backGoodsId
     * @return
     */
    List<BackGoodsItem> queryBackGoodItemsByBackGoodsId(long backGoodsId);


    /**
     * 同意直接退款，这种情况下是货还没有发的情况
     *
     * @param backId 退款单
     * @param userName 操作人
     * @param remark 备注
     */
    void agreeDirectRefund(long backId, String userName, String remark) throws BackGoodsBaseException;


    /**
     * 同意已经发货的退款
     *
     * @param backId 退款单
     * @param userName 操作人
     * @param remark 备注
     */
    void agreeHasSendRefund(long backId, String userName, String remark) throws BackGoodsBaseException;

    /**
     * 根据Id查询BackGoods
     *
     * @param backId
     * @return
     */
    BackGoods queryBackGoodsById(long backId);

    /**
     * 确认退货的商品已经寄回来了，客服需要填写运单号等信息
     *
     * @param backId
     * @param userName
     * @param expressNo
     */
    void confirmBackGoodHasArrivalAtEJS(long backId, String userName, String expressNo) throws BackGoodsBaseException;

    /**
     * 取消或拒绝退货单.
     *
     * @param backId
     * @param userName
     * @param remark
     * @throws BackGoodsBaseException
     */
    void cancelBackGoods(long backId, String userName, String remark) throws BackGoodsBaseException;

    /**
     * 查询所有等待客服审核的退货单数量
     *
     * @return
     */
    int queryBackGoodsCountForWaitingToAudit();

    /**
     * 查询等待客服审核的退货单
     *
     * @return
     */
    List<BackGoods> queryBackGoodsForWaitingToAudit();

    /**
     * 查询等待财务付款的退单数量
     *
     * @return
     */
    int queryWaitPayBackGoodsCountForFinance();

    /**
     * 查询所有需要财务退款的退货单
     *
     * @return
     */
    List<BackGoods> queryWaitPayBackGoodsForFinance();

    /**
     * 查询所有已退款成功的退货单
     *
     * @return
     */
    List<BackGoods> queryPayBackGoodsForFinance();

    /**
     * 退款成功操作.<br/><br/>
     * 1.创建和更新交易信息<br/>
     * 2.更新订单和退单的状态<br/><br/>
     * 其中交易信息在一个事务中, 订单以及退单状态操作失败不影响整体事务
     *
     * @param batchNo 批次号
     * @param successNum 成功数量
     * @param resultDetails 结果详情
     * @return 所有
     */
    List<BackGoods> triggerRefundSuccess(String batchNo, String successNum, String resultDetails);

    /**
     * 自动生成订单.
     *
     * @param order 订单信息
     */
    void automaticOrder(String order) throws OrderBaseException;

    int confirmNotTrueOrder(int day, int limit, String userNameRegex) throws OrderBaseException;
}
