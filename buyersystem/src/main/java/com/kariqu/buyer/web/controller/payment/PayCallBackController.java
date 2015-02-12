package com.kariqu.buyer.web.controller.payment;

import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.tradecenter.payment.CallBackResult;
import com.kariqu.tradecenter.payment.PayResponseHandler;
import com.kariqu.tradecenter.payment.ResponseType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: amos.zhou
 * Date: 13-10-24
 * Time: 上午11:38
 */
@Controller
@RequestMapping("/trade")
public class PayCallBackController {

    private static final Log LOGGER = LogFactory.getLog("TradeReturnLog");

    @RequestMapping(value = "/return")
    @RenderHeaderFooter
    public String normalReturn(HttpServletRequest request, Model model) {
        log(request);

        PayResponseHandler handler = new PayResponseHandler(request);
        CallBackResult result = handler.handleCallback(ResponseType.RETURN);
        model.addAllAttributes(result.getData());
        return result.skipToNextProcess();
    }

    private void log(HttpServletRequest request) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("支付平台返回的数据 : " + request.getParameterMap());
    }

    @RequestMapping(value = "/notify")
    public void notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log(request);

        PayResponseHandler handler = new PayResponseHandler(request);
        CallBackResult result = handler.handleCallback(ResponseType.NOTIFY);
        response.getWriter().write(result.success() ? "success":"fail");
    }
}
