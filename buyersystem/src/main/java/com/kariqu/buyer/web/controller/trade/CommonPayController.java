package com.kariqu.buyer.web.controller.trade;

import com.kariqu.productcenter.service.SkuService;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.OrderItem;
import com.kariqu.tradecenter.service.OrderQueryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 支付公共Controller
 *
 * @author Tiger
 * @version 1.0
 * @since 13-1-21 下午12:30
 */
public class CommonPayController  {
    @Autowired
    protected OrderQueryService orderQueryService;

    @Autowired
    private SkuService skuService;

    protected static final char Log_Gap = '\001';


    protected boolean checkOrderItem(Order order) {
        List<OrderItem> orderItemList = orderQueryService.queryOrderItemsByOrderId(order.getId());
        boolean flag = false;
        for (OrderItem orderItem : orderItemList) {
            //如果是 付款减库存 则检测SKU
            if (orderItem.getStoreStrategy().isPayStrategy()) {
                if (!skuService.isSkuUsable(orderItem.getSkuId())) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }



    protected String buildLog(String successFlag, String type, String tradeNo, String outerTradeNo) {
        StringBuilder sb = new StringBuilder();
        sb.append(successFlag);
        sb.append(Log_Gap);
        sb.append(type);
        sb.append(Log_Gap);
        sb.append("tradeNo=").append(tradeNo);
        sb.append(" outerTradeNo=").append(outerTradeNo);
        return sb.toString();
    }
}
