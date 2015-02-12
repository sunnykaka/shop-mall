package com.kariqu.backgoods.web;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.backgoods.helper.BackSku;
import com.kariqu.backgoods.helper.FinanceOrder;
import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.login.SessionUtils;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.client.TradeCenterBossClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.BackGoodsBaseException;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.service.MessageTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 退货订单
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-28
 *        Time: 上午11:51
 */
@Controller
public class BackGoodsController {

    private final Log logger = LogFactory.getLog(BackGoodsController.class);

    @Autowired
    private TradeCenterBossClient tradeCenterBossClient;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private MessageTaskService messageTaskService;


    /**
     * 查询客服需要处理的退货单
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/backGoods/search")
    public void searchBackOrder(BackGoodsState backState, HttpServletResponse response) throws IOException {
        List<FinanceOrder> orderList = new LinkedList<FinanceOrder>();
        List<BackGoods> backList = tradeCenterBossClient.queryBackGoodsWithBackGoodsState(backState);
        for (BackGoods backGoods : backList) {
            FinanceOrder financeOrder = new FinanceOrder();
            financeOrder.setOrderNo(backGoods.getOrderNo());
            financeOrder.setId(backGoods.getId());
            financeOrder.setWaybillNumber(backGoods.getExpressNo());
            financeOrder.setBackGoodsState(backGoods.getBackState().toString());
            financeOrder.setBackGoodsStateDesc(backGoods.getBackState().serviceDesc());
            financeOrder.setUserName(backGoods.getUserName());
            financeOrder.setBackPhone(backGoods.getBackPhone());
            financeOrder.setPrice(Money.getMoneyString(backGoods.getBackPrice()));
            financeOrder.setBackReason(backGoods.getBackReason());
            financeOrder.setBackReasonReal(backGoods.getBackReasonReal().toDesc());
            financeOrder.setUploadFiles(backGoods.getUploadFiles());
            financeOrder.setHasAttach(backGoods.getUploadFiles() != null);
            financeOrder.setBackDate(DateUtils.formatDate(backGoods.getCreateTime(), DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA));

            Order orderByOrderNo = orderQueryService.getOrderByOrderNo(backGoods.getOrderNo());
            if (orderByOrderNo == null) continue;

            financeOrder.setOrderId(orderByOrderNo.getId());
            financeOrder.setOrderState(orderByOrderNo.getOrderState().toString());
            financeOrder.setOrderStateDesc(orderByOrderNo.getOrderState().serviceDesc());
            financeOrder.setPaybank(orderByOrderNo.getPayBank().toDesc());
            orderList.add(financeOrder);
        }
        new JsonResult(true).addData("totalCount", backList.size()).addData("result", orderList).toJson(response);
    }

    /**
     * 退货项
     *
     * @param backId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/backOrder/{backId}")
    public void skuGrid(@PathVariable("backId") long backId, HttpServletResponse response) throws IOException {
        List<BackSku> list = new LinkedList<BackSku>();
        List<BackGoodsItem> backGoodsItems = tradeCenterBossClient.queryBackGoodItemsByBackGoodsId(backId);
        for (BackGoodsItem backGoodsItem : backGoodsItems) {
            BackSku backSku = new BackSku();
            backSku.setNumber(backGoodsItem.getNumber());
            OrderItem orderItem = orderQueryService.queryOrderItemsById(backGoodsItem.getOrderItemId());
            long unitPrice = orderItem.getUnitPrice();
            Money money = new Money();
            money.setCent(unitPrice);
            backSku.setSkuPrice(money.toString());
            backSku.setProductName(orderItem.getSkuName());
            backSku.setAttribute(orderItem.getSkuExplain());
            list.add(backSku);
        }
        new JsonResult(true).addData("totalCount", list.size()).addData("result", list).toJson(response);
    }

    /**
     * 同意退货
     *
     * @param backId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/backOrder/accept", method = RequestMethod.POST)
    @Permission("客服审核通过了退货单")
    public void acceptBack(@RequestParam("backId") long backId, boolean notSend, String remark, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Account loginAccount = SessionUtils.getLoginAccount(request.getSession());
        try {
            if (notSend) {
                tradeCenterBossClient.agreeDirectRefund(backId, loginAccount.getUserName(), remark);
            } else {
                tradeCenterBossClient.agreeHasSendRefund(backId, loginAccount.getUserName(), remark);
                BackGoods backGoods = tradeCenterBossClient.queryBackGoodsById(backId);
                if (backGoods != null) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userName", backGoods.getBackShopperName());
                    params.put("orderNo", backGoods.getId().toString());
                    messageTaskService.sendSmsMessage(backGoods.getBackPhone(), params, MessageTemplateName.BACKGOODS_AUDIT_PASS);
                }
            }
        } catch (Exception e) {
            logger.error("客服同意退货，操作数据库失败：" + e);
            new JsonResult(false, "操作失败").toJson(response);
        }
    }

    /**
     * 客服填入用户寄回来的运单号，退货完成
     *
     * @param backId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/backOrder/getGood", method = RequestMethod.POST)
    @Permission("客服填入收货运单号")
    public void getGood(@RequestParam("backId") long backId, @RequestParam("expressNo") String expressNo, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Account loginAccount = SessionUtils.getLoginAccount(request.getSession());
        try {
            tradeCenterBossClient.confirmBackGoodHasArrivalAtEJS(backId, loginAccount.getUserName(), expressNo);
            new JsonResult(true, "操作成功").toJson(response);
        } catch (Exception e) {
            logger.error("客服收到退货商品，填了运单号，操作数据库失败：" + e);
            new JsonResult(false, "操作失败").toJson(response);

        }
    }

    /**
     * 取消退货
     *
     * @param backId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/backOrder/cancel", method = RequestMethod.POST)
    @Permission("客服取消了退货")
    public void cancelBack(@RequestParam("backId") long backId, @RequestParam("remark") String remark, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Account loginAccount = SessionUtils.getLoginAccount(request.getSession());
        try {
            try {
                tradeCenterBossClient.cancelBackGoods(backId, remark, loginAccount.getUserName());
            } catch (BackGoodsBaseException e) {
                new JsonResult(false, e.getMessage()).toJson(response);
            }
        } catch (Exception e) {
            logger.error("客服拒绝了退货，操作数据库失败：" + e);
            new JsonResult(false, "操作失败").toJson(response);

        }
    }

    /**
     * 客服需要处理的退货单数量，会频繁的调用
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/backOrder/count")
    public void backOrderCount(HttpServletResponse response) throws IOException {
        new JsonResult(true).addData("orderCount", tradeCenterBossClient.queryBackGoodsCountForWaitingToAudit()).toJson(response);
    }

    /**
     * 输出退单的附件图片
     *
     * @param backId
     * @param response
     * @throws IOException
     */
   /* @RequestMapping(value = "/order/backOrder/img/{backId}")
    public void backAttachImg(@PathVariable("backId") long backId, HttpServletResponse response) throws IOException {
        BackGoods backGoods = tradeCenterBossClient.queryBackGoodsById(backId);
        InputStream inputStream = new ByteArrayInputStream(backGoods.getUploadFile());
        // 将文件内容拷贝到一个输出流中
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }*/

    /**
     * 下载退单的附件
     *
     * @param backId
     * @param response
     * @throws IOException
     */
    /*@RequestMapping(value = "/order/backOrder/file/{backId}")
    public void backAttachFile(@PathVariable("backId") long backId, HttpServletResponse response) throws IOException {
        BackGoods backGoods = tradeCenterBossClient.queryBackGoodsById(backId);

        response.setCharacterEncoding("UTF-8");
        response.setContentType(backGoods.getFileType());
        response.setHeader("Content-Disposition", "attachment; filename=" + backId);

        InputStream inputStream = new ByteArrayInputStream(backGoods.getUploadFile());
        // 将文件内容拷贝到一个输出流中
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }*/


}
