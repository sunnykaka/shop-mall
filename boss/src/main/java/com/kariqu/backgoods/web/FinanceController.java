package com.kariqu.backgoods.web;

import com.kariqu.backgoods.helper.FinanceOrder;
import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.RequestUtil;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.client.TradeCenterBossClient;
import com.kariqu.tradecenter.domain.BackGoods;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.RefundTradeOrder;
import com.kariqu.tradecenter.helper.TradeSequenceUtil;
import com.kariqu.tradecenter.payment.alipay.AliPayRequestHandler;
import com.kariqu.tradecenter.payment.alipay.AlipayUtil;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.tradecenter.service.TradeService;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.service.MessageTaskService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-28
 *        Time: 上午11:51
 */
@Controller
public class FinanceController {

    private final Log alipayLog = LogFactory.getLog("alipayMsg");

    private static final Logger logger = Logger.getLogger(FinanceController.class);

    @Autowired
    private TradeCenterBossClient tradeCenterBossClient;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private MessageTaskService messageTaskService;


    @RequestMapping(value = "/finance/refundment/search")
    public void searchOrder(HttpServletResponse response, String action) throws IOException {
        List<FinanceOrder> orderList = new LinkedList<FinanceOrder>();
        List<BackGoods> backList = Collections.emptyList();
        if ("WaitRefund".equalsIgnoreCase(action)) {
            backList = tradeCenterBossClient.queryWaitPayBackGoodsForFinance();
        } else if ("Success".equalsIgnoreCase(action)) {
            backList = tradeCenterBossClient.queryPayBackGoodsForFinance();
        }
        for (BackGoods backGoods : backList) {
            Order orderByOrderNo = orderQueryService.getOrderByOrderNo(backGoods.getOrderNo());
            if (orderByOrderNo == null) continue;

            FinanceOrder financeOrder = new FinanceOrder();
            financeOrder.setOrderNo(backGoods.getOrderNo());
            financeOrder.setId(backGoods.getId());
            financeOrder.setBackGoodsState(backGoods.getBackState().financeDesc());
            financeOrder.setCanRefund(!backGoods.getBackType().checkCanNotRefunds(backGoods.getBackState()));

            financeOrder.setPaybank(orderByOrderNo.getPayBank().toDesc());
            financeOrder.setUserName(backGoods.getUserName());
            financeOrder.setOrderId(orderByOrderNo.getId());
            financeOrder.setInvoice(orderByOrderNo.getInvoiceInfo().isInvoice());
            financeOrder.setBackDate(DateUtils.formatDate(backGoods.getCreateTime(), DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA));


            Money money = new Money();
            money.setCent(backGoods.getBackPrice());
            financeOrder.setPrice(money.toString());
            financeOrder.setBackReason(backGoods.getBackReason());
            financeOrder.setOuterTradeNo(tradeService.queryOuterTradeNoByOrderNo(orderByOrderNo.getOrderNo()));
            orderList.add(financeOrder);
        }
        new JsonResult(true).addData("totalCount", backList.size()).addData("result", orderList).toJson(response);
    }


    /**
     * 财务提交退款
     *
     * @throws IOException
     */
    @RequestMapping(value = "/finance/refundment/submit")
    @Permission("财务操作退款")
    public String submitRefundment(long backId, String outerTradeNo, String price, String reason, Model model, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");

        BackGoods backGoods = tradeCenterBossClient.queryBackGoodsById(backId);
        if (backGoods == null) {
            response.getWriter().print("没有这个退货单");
            return null;
        }
        if (StringUtils.isEmpty(outerTradeNo) || StringUtils.isEmpty(price) || StringUtils.isEmpty(reason)) {
            response.getWriter().print("交易号，理由，退款金额不能为空");
            return null;
        }
        if (backGoods.checkCanNotRefunds()) {
            response.getWriter().print("请只操作等待退款的退单");
            return null;
        }

        RefundTradeOrder refundTradeOrder = tradeService.queryRefundTradeOrderByBackGoodsId(backId);

        // 为空 或 批次号不是今天的, 则创建新的批次号
        if (refundTradeOrder == null || TradeSequenceUtil.checkRefundBatchNoIsNotToday(refundTradeOrder.getBatchNo())) {
            String refundBatchNo = TradeSequenceUtil.getRefundBatchNo();
            refundTradeOrder = new RefundTradeOrder();
            refundTradeOrder.setBackGoodsId(backId);
            refundTradeOrder.setBatchNo(refundBatchNo);
            refundTradeOrder.setOuterTradeNo(outerTradeNo);
            refundTradeOrder.setRefund(new Money(price).getCent());
            tradeService.createRefundTradeOrder(refundTradeOrder);
        }

        if (refundTradeOrder.isSuccess()) {
            response.getWriter().write("这个退单已经退款成功");
            return null;
        } else {
            Map<String, String> stringStringMap = AliPayRequestHandler.buildRefundParam(refundTradeOrder.getBatchNo(), outerTradeNo, price, reason);
            if (alipayLog.isWarnEnabled()) {
                alipayLog.warn("向支付宝发起退款: " + stringStringMap);
            }

            StringBuilder sbHtml = new StringBuilder();
            sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + AlipayUtil.ALIPAY_GATEWAY_NEW + "\" method=\"get\">");
            for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                sbHtml.append("<input type=\"hidden\" name=\"").append(entry.getKey()).append("\" value=\"").append(entry.getValue()).append("\"/>");
            }
            sbHtml.append("<input type=\"submit\" value=\"" + "确认" + "\" style=\"display:none;\"></form>");
            sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
            model.addAttribute("form", sbHtml.toString());
            return "alipayTo";
        }
    }

    /**
     * 退款通知接口
     *
     * @throws IOException
     */
    @RequestMapping(value = "/alipay/refund/notify", method = RequestMethod.POST)
    @Permission("退款成功")
    public void refundNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (alipayLog.isWarnEnabled()) {
            alipayLog.warn("支付宝返回退款通知: " + RequestUtil.getRequestParams(request));
        }

        String batch_no = request.getParameter("batch_no");
        String success_num = request.getParameter("success_num");
        String result_details = request.getParameter("result_details");

        if (StringUtils.isEmpty(batch_no) || StringUtils.isEmpty(success_num) || StringUtils.isEmpty(result_details)) {
            logger.error("支付宝返回的信息没有读取到有用信息");
            response.getWriter().write("fail");
            return;
        }

        try {
            List<BackGoods> successBackList = tradeCenterBossClient.triggerRefundSuccess(batch_no, success_num, result_details);
            for (BackGoods backGoods : successBackList) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", backGoods.getBackShopperName());
                params.put("orderNo", backGoods.getOrderNo().toString());
                params.put("backPrice", backGoods.getBackPriceByMoney());
                messageTaskService.sendSmsMessage(backGoods.getBackPhone(), params, MessageTemplateName.REFUND_SUCCESS);
            }
        } catch (Exception e) {
            response.getWriter().write("fail");
            return;
        }
        response.getWriter().write("success");
    }

    /**
     * 财务需要处理的退款数量
     *
     * @throws IOException
     */
    @RequestMapping(value = "/finance/redfund/count")
    public void refundOrderCount(HttpServletResponse response) throws IOException {
        new JsonResult(true).addData("orderCount", tradeCenterBossClient.queryWaitPayBackGoodsCountForFinance()).toJson(response);
    }

}
