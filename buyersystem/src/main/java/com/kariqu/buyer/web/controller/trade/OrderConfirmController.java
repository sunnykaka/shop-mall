package com.kariqu.buyer.web.controller.trade;

import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 订单自动确认完成，通过客户端定时请求
 * User: Asion
 * Date: 13-5-15
 * Time: 下午2:23
 */
@Controller
public class OrderConfirmController {

    private static Log logger = LogFactory.getLog(OrderConfirmController.class);


    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;


    @RequestMapping(value = "/confirmOrder")
    public void confirmOrder(HttpServletResponse response) throws IOException {
        List<Long> longs = tradeCenterUserClient.queryNotSuccessOrder(10);
        for (Long aLong : longs) {
            try {
                tradeCenterUserClient.confirmOrderSuccess(aLong);
            } catch (OrderBaseException e) {
                logger.error("系统自动确认收货时发生异常", e);
            }
        }
        response.getWriter().write("success");
    }


}
