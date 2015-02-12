package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.BackGoodsNoTransactionalException;
import com.kariqu.tradecenter.repository.BackGoodsItemRepository;
import com.kariqu.tradecenter.repository.BackGoodsLogRepository;
import com.kariqu.tradecenter.repository.BackGoodsRepository;
import com.kariqu.tradecenter.service.BackGoodsQueryService;
import com.kariqu.tradecenter.service.OrderQueryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Athens(刘杰)
 * @Time 2013-01-21 18:09
 * @since 1.0.0
 */
public class BackGoodsQueryServiceImpl implements BackGoodsQueryService {

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private BackGoodsRepository backGoodsRepository;

    @Autowired
    private BackGoodsItemRepository backGoodsItemRepository;

    @Autowired
    private BackGoodsLogRepository backGoodsLogRepository;

    @Override
    public Result<OrderItem, Order> queryCanBackOrderItemsOfUser(long orderNo, int userId) throws BackGoodsNoTransactionalException {
        // 检查退货单是否需要退货
        Order order = orderQueryService.getOrderByOrderNo(orderNo);
        if (order == null || userId != order.getUserId()) {
            throw new BackGoodsNoTransactionalException("您没有此订单(" + orderNo + ")!");
        }
        if (order.getOrderState().checkCanNotBack()) {
            throw new BackGoodsNoTransactionalException("订单(" + orderNo + ")不需要退货!");
        }
        if (Money.YuanToCent(order.getTotalPrice()) == 0) {
            throw new BackGoodsNoTransactionalException("订单(" + orderNo + ")的付款金额为 0, 不需要退货!");
        }

        // 查询已退过货的订单
        List<BackGoods> existBackGoods = queryBackGoodsByOrderNoAndUserId(orderNo, userId);
        Map<Long, BackGoodsItem> existBackGoodsItems = new HashMap<Long, BackGoodsItem>();
        for (BackGoods existBackGood : existBackGoods) {
            List<BackGoodsItem> backGoodsItems = queryBackGoodsItemByBackGoodsId(existBackGood.getId());
            for (BackGoodsItem item : backGoodsItems) {
                // 同一个订单对应的sku(orderItemId) 只能退货一次
                existBackGoodsItems.put(item.getOrderItemId(), item);
            }
        }
        // 订单对应的详情列表
        List<OrderItem> orderItems = orderQueryService.queryOrderItemsByOrderId(order.getId());
        List<OrderItem> canBackItems = new LinkedList<OrderItem>();
        for (OrderItem item : orderItems) {
            if (!existBackGoodsItems.containsKey(item.getId())) {
                canBackItems.add(item);
            }
        }
        if (canBackItems.size() == 0) {
            throw new BackGoodsNoTransactionalException("请确认订单(" + orderNo + ")是否已经退过货!");
        }

        return new Result<OrderItem, Order>(true, canBackItems, order, StringUtils.EMPTY);
    }

    @Override
    public Progress queryBackProgress(long backGoodsId) {
        Progress progress = new Progress();
        List<ProgressDetail> lightenProgressList = new ArrayList<ProgressDetail>();
        List<String> greyProgressList = new ArrayList<String>();

        BackGoods backGoods = backGoodsRepository.select(backGoodsId);
        switch (backGoods.getBackState()) {
            case Create : {
                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Create, "创建退货单"));

                ProgressDetail lightenWait = new ProgressDetail();
                lightenWait.setDetail("等待审核");
                lightenProgressList.add(lightenWait);

                if (backGoods.getBackType() == BackGoodsState.BackGoodsType.YetSend)
                    greyProgressList.add("等待收货");

                greyProgressList.add("已退款");
                break;
            }
            case Verify : {
                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Create, "创建退货单"));

                String doWhat = (backGoods.getBackType() == BackGoodsState.BackGoodsType.YetSend) ? "" : ",等待退款";
                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Verify, "审核通过" + doWhat));

                if (backGoods.getBackType() == BackGoodsState.BackGoodsType.YetSend) {
                    ProgressDetail progressDetail = new ProgressDetail();
                    progressDetail.setDetail("等待客户将货发回");
                    lightenProgressList.add(progressDetail);
                }

                greyProgressList.add("已退款");
                break;
            }
            case Receive : {
                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Create, "创建退货单"));
                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Verify, "审核通过"));

                if (backGoods.getBackType() == BackGoodsState.BackGoodsType.YetSend)
                    lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Receive, "确认收货,等待退款"));

                greyProgressList.add("已退款");
                break;
            }
            case Success : {
                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Create, "创建退货单"));
                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Verify, "审核通过"));

                if (backGoods.getBackType() == BackGoodsState.BackGoodsType.YetSend) {
                    lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Receive, "确认收货"));
                }

                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Success, "已退款"));
                break;
            }
            case Cancel : {
                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Create, "创建退货单"));
                lightenProgressList.add(getProgressDetailByBackGoodsIdAndState(backGoodsId, BackGoodsState.Cancel, "已取消"));
                break;
            }
        }

        progress.setLightenProgress(lightenProgressList);
        progress.setGreyProgress(greyProgressList);
        return progress;
    }

    private ProgressDetail getProgressDetailByBackGoodsIdAndState(long backGoodsId, BackGoodsState backState, String detail) {
        ProgressDetail lighten = new ProgressDetail();
        BackGoodsLog log = queryBackGoodsLogByState(backGoodsId, backState);
        lighten.setDetail(detail);
        lighten.setDate(DateUtils.formatDate(log.getOperaTime(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        return lighten;
    }

    @Override
    public BackGoodsLog queryBackGoodsLogByState(long backGoodsId, BackGoodsState backState) {
        return backGoodsLogRepository.selectByState(backGoodsId, backState);
    }

    public BackGoodsLog queryRecentBackGoodsLogByOrderId(long orderId) {
        return backGoodsLogRepository.selectRecentLogByOrderId(orderId);
    }

    @Override
    public int queryBackGoodsCountForWaitingToAudit() {
        return backGoodsRepository.selectCountByState(BackGoodsState.Create);
    }

    @Override
    public List<BackGoods> queryBackGoodsByState(BackGoodsState state) {
        return backGoodsRepository.selectByState(state);
    }

    @Override
    public int queryBackGoodsCountForFinance() {
        return backGoodsRepository.selectCountByFinance();
    }

    @Override
    public List<BackGoods> queryBackGoodsForFinance() {
        return backGoodsRepository.selectForFinance();
    }

    @Override
    public List<BackGoods> queryBackGoodsByUserId(int userId) {
        return backGoodsRepository.selectByUserId(userId);
    }

    @Override
    public List<BackGoods> queryBackGoodsByOrderId(long orderId) {
        return backGoodsRepository.selectByOrderId(orderId);
    }

    @Override
    public List<BackGoods> queryBackGoodsByOrderNoAndUserId(long orderNo, int userId) {
        // 把所有已取消的排除在外.
        return backGoodsRepository.selectByOrderNo(orderNo, userId);
    }

    @Override
    public List<Long> queryBackGoodsIdByOrderNoAndUserId(long orderNo, int userId) {
        // 把所有已取消的排除在外.
        return backGoodsRepository.selectIdByOrderNo(orderNo, userId);
    }


    @Override
    public Page<BackGoods> queryBackGoodsByUserIdForPage(int userId, Page<BackGoods> page) {
        Page<BackGoods> backGoodsPage = backGoodsRepository.queryBackGoodsByUserIdPage(userId, page);
        List<BackGoods> backGoodsList = backGoodsPage.getResult();
        for (BackGoods backGoods : backGoodsList) {
            backGoods.setBackGoodsItemList(queryBackGoodsItemByBackGoodsId(backGoods.getId()));
        }
        return backGoodsPage;
    }

    @Override
    public BackGoods queryBackGoodsById(long backGoodsId) {
        return backGoodsRepository.select(backGoodsId);
    }

    @Override
    public BackGoods queryBackGoodsAndItemByIdAndUserId(int userId, long backGoodsId) {
        BackGoods backGoods = backGoodsRepository.select(userId, backGoodsId);
        if (backGoods != null) {
            backGoods.setBackGoodsItemList(queryBackGoodsItemByBackGoodsId(backGoodsId));
        }
        return backGoods;
    }

    @Override
    public List<BackGoodsItem> queryBackGoodsItemByBackGoodsId(long backGoodsId) {
        List<BackGoodsItem> backGoodsItems = backGoodsItemRepository.selectByBackGoodsId(backGoodsId);
        for (BackGoodsItem backGoodsItem : backGoodsItems) {
            backGoodsItem.setOrderItem(orderQueryService.queryOrderItemsById(backGoodsItem.getOrderItemId()));
        }
        return backGoodsItems;
    }

    @Override
    public int queryByBackOrderItemId(Long backGoodsId, Long orderItemId) {
        return backGoodsItemRepository.selectByBackOrderItemId(backGoodsId, orderItemId);
    }

    @Override
    public List<BackGoodsLog> queryBackGoodsLogByBackGoodsIdForUser(long backGoodsId) {
        return backGoodsLogRepository.selectByBackId(backGoodsId);
    }

}
