package com.kariqu.tradesystem.web;

import com.google.common.base.Joiner;
import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.service.BackGoodsQueryService;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.tradecenter.service.TradeQuery;
import com.kariqu.tradecenter.service.TradeService;
import com.kariqu.tradesystem.helper.ExportTradeExcel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * User: Json.zhu
 * Date: 13-12-9
 * Time: 15:47
 */
@Controller
public class TradeDisplayController {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private BackGoodsQueryService backGoodsQueryService;


    @RequestMapping(value = "/trade/search")
    public void tradeInfoSearchByConditions(TradeQuery tradeQuery, HttpServletResponse response) throws IOException {
        Page<TradeInfo> tradePage = tradeService.getTradeByConditions(tradeQuery);
        List<TradeInfo> tradeInfoList = tradePage.getResult();
        for (TradeInfo tradeInfo : tradeInfoList) {
            List<Long> orderNoList = tradeService.queryOrderListByTradeNoAndPayFlag(tradeInfo.getTradeNo());
            tradeInfo.setOrderNo(Joiner.on(",").skipNulls().join(orderNoList));
        }
        new JsonResult(true).addData("totalCount", tradePage.getTotalCount()).addData("result", tradeInfoList).toJson(response);
    }

    /**
     * 根据制定的条件导出数据：业务方式bizType，支付凡是payMethod，创建时间gmtCreateTime
     * 修改人：Json.zhu
     * 修改时间:2013.12.10,16:14
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/trade/export")
    public void exportTrade(TradeQuery tradeQuery, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, List> exportTradeMap = new LinkedHashMap<String, List>();

        exportTradeMap.put("付款数据", tradeExcel(tradeQuery)); //交易成果的数据
        exportTradeMap.put("退款数据", refundTrade(tradeQuery.getStartDate(), tradeQuery.getEndDate()));//退款数据
        //退款数据
        String[] headerListTrade = new String[]{"交易号", "流水号", /*"业务类型",*/ "支付方式", "总金额", "订单号",
                "订单项", "单价", "购买数量", "应付金额", "付款金额", "订单价格说明"};
        String[] fieldNameListTrade = new String[]{"tradeNo|F", "outerTradeNo|F", /*"bizType|F",*/ "payType|F", "tradeTotalFee|F", "orderNo|S",
                "skuName|N", "unitPrice|N", "skuNum|N", "skuTotalFee|N", "orderTotalFee|S", "priceMessageDetail|S"};

        String[] headerListRefund = new String[]{"退款批次号", "实际的退款金额", "退款对应的支付流水号", "退款单编号", "订单编号",
                "订单项",  "单价", "购买数量", "退货数量", "订单退款金额", "退款原因", "订单价格说明"};
        String[] fieldNameListRefund = new String[]{"batchNo|F", "realRefundTotalFee|F", "outerTradeNo|S", "backGoodsId|S", "orderNo|S",
                "skuName|N", "unitPrice|N", "skuNum|N", "refundSkuNum|N", "backPrice|S", "backReason|S", "priceMessageDetail|S"};

        List<String[]> headerList = new LinkedList<String[]>();
        headerList.add(headerListTrade);
        headerList.add(headerListRefund);
        List<String[]> fieldNameList = new LinkedList<String[]>();
        fieldNameList.add(fieldNameListTrade);
        fieldNameList.add(fieldNameListRefund);


        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFileName(request, "交易数据") + ".xls\"");
        response.setContentType("application/vnd.ms-excel");
        ExportTradeExcel.exportExcel(exportTradeMap, headerList, fieldNameList, response.getOutputStream());
    }

    /**
     * 成功交易的数据
     *
     * @param tradeQuery
     * @return
     */
    private List<ExportTrade> tradeExcel(TradeQuery tradeQuery) {
        List<TradeInfo> tradeInfoList = tradeService.getTradeByListConditions(tradeQuery);   //交易数据
        List<ExportTrade> exportTradeList = new ArrayList<ExportTrade>();
        for (TradeInfo trade : tradeInfoList) {
            if ("coupon".equalsIgnoreCase(trade.getBizType())) {
                ExportTrade etList = new ExportTrade();
                etList.setTradeNo(trade.getTradeNo()); //交易号
                etList.setOuterTradeNo(trade.getOuterTradeNo()); //流水号
                etList.setBizType("现金券"); //业务类型
                etList.setTradeTotalFee(trade.getPayTotalFeeTOString());//总金额
                etList.setPayType(trade.getPayType());//支付方式
                exportTradeList.add(etList);
            } else {
                List<Long> orderNoList = tradeService.queryOrderListByTradeNoAndPayFlag(trade.getTradeNo());
                int countTrade = 0, countTradeSum = 0;
                for (Long orderNo : orderNoList) {
                    Order order = orderQueryService.getSimpleOrderByOrderNo(orderNo);
                    if (order == null) continue;
                    List<OrderItem> orderItemList = orderQueryService.queryOrderItemsByOrderId(order.getId());
                    countTradeSum += orderItemList.size();

                    int countOrder = 0;
                    for (OrderItem orderItem : orderItemList) {
                        ExportTrade etList = new ExportTrade();

                        etList.setSkuName(orderItem.getSkuName());//订单项（商品名字）
                        etList.setUnitPrice(orderItem.getUnitPriceByMoney());//订单的单价
                        etList.setSkuNum(orderItem.getNumberByString());//订单的数量
                        etList.setSkuTotalFee(orderItem.getSubtotalPrice());//订单应付金额

                        if (countOrder == 0) {
                            etList.setOrderNo(orderNo.toString()); //订单号
                            etList.setOrderTotalFee(order.getTotalPrice());//订单应付款（实际付款金额）
                            etList.setPriceMessageDetail(order.getPriceMessageDetail());//  订单价格说明
                        }
                        if (countTrade == 0) {
                            etList.setTradeNo(trade.getTradeNo()); //交易号
                            etList.setOuterTradeNo(trade.getOuterTradeNo()); //流水号
                            etList.setBizType("订单"); //业务类型
                            etList.setTradeTotalFee(trade.getPayTotalFeeTOString());//总金额
                            etList.setPayType(trade.getPayType());//支付方式
                        }
                        countOrder++;
                        countTrade++;
                        if (countOrder == orderItemList.size())
                            etList.setCountSecond(countOrder);
                        exportTradeList.add(etList);
                    }
                }
                if (countTradeSum > 1) {
                    exportTradeList.get(exportTradeList.size() - 1).setCountFirst(countTradeSum);
                }
            }
        }
        return exportTradeList;
    }

    /**
     * 成功退款数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private List<ExportRefundTrade> refundTrade(String startDate, String endDate) {
        List<RefundTrade> tradeInfoList = tradeService.getRefundTradeAllInfo(startDate, endDate);   //交易数据
        List<ExportRefundTrade> exportRefundTradeList = new ArrayList<ExportRefundTrade>();
        for (RefundTrade refundTrade : tradeInfoList) {
            List<RefundTradeOrder> refundTradeOrderList = tradeService.getBackIdByBatchNo(refundTrade.getBatchNo());
            int countRefund = 0, countRefundSum = 0;
            for (RefundTradeOrder rto : refundTradeOrderList) {
                List<BackGoodsItem> backGoodsItems = backGoodsQueryService.queryBackGoodsItemByBackGoodsId(rto.getBackGoodsId());
                countRefundSum += backGoodsItems.size();

                BackGoods backGoods = backGoodsQueryService.queryBackGoodsById(rto.getBackGoodsId());
                int countOrder = 0;
                for (BackGoodsItem bgi : backGoodsItems) {
                    ExportRefundTrade exportRefundTrade = new ExportRefundTrade();
                    OrderItem orderItem = orderQueryService.queryOrderItemsById(bgi.getOrderItemId());
                    exportRefundTrade.setSkuName(orderItem.getSkuName());//订单项（订单名字）
                    exportRefundTrade.setUnitPrice(orderItem.getUnitPriceByMoney());//退款的商品的单价
                    exportRefundTrade.setSkuNum(Long.toString(orderItem.getNumber()));//订单数目
                    exportRefundTrade.setRefundSkuNum(bgi.getNumberByString());//实际的sku退款数量

                    Order order = orderQueryService.getSimpleOrder(orderItem.getOrderId());
                    exportRefundTrade.setPriceMessageDetail(order.getPriceMessageDetail());//订单价格说明
                    if (countOrder == 0) {
                        exportRefundTrade.setOrderNo(Long.toString(backGoods.getOrderNo()));//订单编号
                        exportRefundTrade.setBackPrice(backGoods.getBackPriceByMoney());//退款金额
                        exportRefundTrade.setBackReason(backGoods.getBackReason());//退款理由
                        exportRefundTrade.setOuterTradeNo(rto.getOuterTradeNo());//流水号
                        exportRefundTrade.setBackGoodsId(Long.toString(rto.getBackGoodsId()));//退款的编号
                    }
                    if (countRefund == 0) {
                        exportRefundTrade.setBatchNo(refundTrade.getBatchNo());//退款批次号
                        exportRefundTrade.setRealRefundTotalFee(refundTrade.getRefundPrice());//退款金额
                    }
                    countOrder++;
                    countRefund++;
                    if (countOrder == backGoodsItems.size())
                        exportRefundTrade.setCountSecond(countOrder);
                    exportRefundTradeList.add(exportRefundTrade);
                }

            }
            if (countRefundSum > 1) {
                exportRefundTradeList.get(exportRefundTradeList.size() - 1).setCountFirst(countRefundSum);
            }
        }
        return exportRefundTradeList;
    }

    private String encodeFileName(HttpServletRequest request, String name) {
        String fileName = name + "-" + DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
        try {
            String userAgent = request.getHeader("USER-AGENT");
            if (StringUtils.contains(userAgent, "MSIE")) {
                fileName = URLEncoder.encode(fileName, "UTF8");
            } else if (StringUtils.contains(userAgent, "Mozilla")) {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } else {
                fileName = URLEncoder.encode(fileName, "UTF8");
            }
        } catch (UnsupportedEncodingException e) {
        }
        return fileName;
    }
}