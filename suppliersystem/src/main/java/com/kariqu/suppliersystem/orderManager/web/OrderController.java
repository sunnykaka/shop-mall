package com.kariqu.suppliersystem.orderManager.web;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.LogisticsPrintInfo;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliercenter.domain.SupplierLog;
import com.kariqu.suppliersystem.common.JsonResult;
import com.kariqu.suppliersystem.orderManager.logisticsGenerator.LogisticsGeneratorFactory;
import com.kariqu.suppliersystem.orderManager.logisticsGenerator.LogisticsNumGenerator;
import com.kariqu.suppliersystem.orderManager.vo.OrderPrintInfo;
import com.kariqu.suppliersystem.orderManager.vo.PlatformOrder;
import com.kariqu.suppliersystem.orderManager.vo.PlatformOrderItem;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.excepiton.OrderTransactionalException;
import com.kariqu.tradecenter.service.Query;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.service.MessageTaskService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * order_list（订单列表）页面功能
 *
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-29
 * Time: 下午4:23
 */
@Controller
@RequestMapping("/orders")
public class OrderController extends BaseController {


    private final Log logger = LogFactory.getLog(OrderController.class);


    @Autowired
    private MessageTaskService messageTaskService;

    private boolean developMode;

    /**
     * 根据query对象查询条件查询订单
     *
     * @param query
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public void queryOrder(Query query, HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (query.getOrderState() == null) {
            query.setOrderState(OrderState.Confirm);
        } else if (query.getOrderState() == OrderState.Success) {
            query.setOrderState(null);
        }
        try {
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            query.setCustomerId(supplierAccount.getCustomerId());
            List<PlatformOrder> platformOrders = getPlatformOrderByQuery(query);
            new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_LIST, platformOrders).addData(JsonResult.RESULT_TYPE_TOTAL_COUNT, tradeCenterSupplierClient.searchQuery(query, query.getCustomerId(), new Page<Order>(0, query.getLimit())).getTotalCount()).toJson(response);
        } catch (Exception e) {
            logger.error("获取订单数据出错", e);
            new JsonResult(JsonResult.FAILURE, "获取订单数据出错").toJson(response);
        }

    }


    /**
     * 批量给订单生成物流单号
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public void batchWaybillNum(int[] orderIds, DeliveryInfo deliveryInfo, String waybillNumber, HttpServletResponse response, HttpServletRequest request) throws IOException {
        List<String> waybillNumbers;
        try {
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            LogisticsPrintInfo logisticsPrintInfo = queryLogisticsPrintInfo(supplierAccount.getCustomerId(), supplierAccount.getAccountName(), deliveryInfo.getDeliveryType());
            if (null == logisticsPrintInfo) {
                new JsonResult(JsonResult.FAILURE, "该快递公司没有进行物流设计，请添加").toJson(response);
                return;
            }

            LogisticsNumGenerator generator = LogisticsGeneratorFactory.getGenerator(deliveryInfo.getDeliveryType());
            waybillNumbers = generator.generateNumList(waybillNumber, logisticsPrintInfo.getLaw(), orderIds.length);
            for (int i = 0; i < orderIds.length; i++) {
                Logistics logistics = tradeCenterSupplierClient.getLogisticsByOrderId(orderIds[i]);
                DeliveryInfo info = logistics.getDeliveryInfo();
                info.setWaybillNumber(waybillNumbers.get(i));
                logistics.setDeliveryInfo(info);
                tradeCenterSupplierClient.updateLogistics(logistics);
            }
        } catch (Exception e) {
            logger.error("生成物流单号出错,错误信息为：" + e.getMessage(), e);
            new JsonResult(JsonResult.FAILURE, e.getMessage()).toJson(response);
            return;
        }
        new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_LIST, waybillNumbers).toJson(response);
    }


    /**
     * 根据订单Id查询订单详细信息
     *
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET)
    public void queryOrderDetailByOrderId(@PathVariable("orderId") long orderId, HttpServletResponse response) throws IOException {
        try {
            PlatformOrder platformOrder = getPlatformOrderById(orderId);
            //不需要发票,清空发票信息。避免前端EXTJS判断，起因：前端extjs无法根据是否需要发票做出不停的响应
            if (!platformOrder.isInvoice()) {
                platformOrder.setInvoiceContent("");
                platformOrder.setInvoiceTitle("");
                platformOrder.setInvoiceType("");
            }
            new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_SINGLE_OBJECT, platformOrder).toJson(response);
        } catch (Exception e) {
            logger.error("获取订单详情失败", e);
            new JsonResult(JsonResult.FAILURE, "获取订单详情失败").toJson(response);
            ;
        }
    }


    /**
     * 修改订单的物流单号
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/{orderId}", method = RequestMethod.POST)
    public void updateWaybillNumber(@PathVariable("orderId") long orderId, @RequestParam("waybillNumber") String waybillNumber, HttpServletResponse response) throws IOException {
        try {
            Logistics logistics = tradeCenterSupplierClient.getLogisticsByOrderId(orderId);
            DeliveryInfo info = logistics.getDeliveryInfo();
            info.setWaybillNumber(waybillNumber);
            logistics.setDeliveryInfo(info);
            tradeCenterSupplierClient.updateLogistics(logistics);
        } catch (Exception e) {
            logger.error("生成订单号出错：", e);
            new JsonResult(JsonResult.FAILURE, "修改订单号失败").toJson(response);
            return;
        }
        new JsonResult(JsonResult.SUCCESS).toJson(response);
    }


    /**
     * 获取订单的订单项
     *
     * @param orderId  订单Id
     * @param response
     */
    @RequestMapping("/{orderId}/items")
    public void orderItemList(@PathVariable("orderId") long orderId, HttpServletResponse response) throws IOException {
        List<PlatformOrderItem> orderItems = getPlatformOrderItemByOrderId(orderId);
        new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_LIST, orderItems).toJson(response);
        ;
    }


    /**
     * 根据订单Id查询发货单信息
     *
     * @param orderIds
     * @return
     */
    @RequestMapping(value = "/delivery_info", method = RequestMethod.POST)
    public void queryInvoiceByOrderId(@RequestParam("orderIds") long[] orderIds, HttpServletResponse response) throws IOException {
        try {
            List<OrderPrintInfo> orderPrintInfoList = new ArrayList<OrderPrintInfo>();
            List<PlatformOrder> PlatformOrders = new ArrayList<PlatformOrder>();
            for (long orderId : orderIds) {
                Order order = tradeCenterSupplierClient.getSimpleOrder(orderId);
                OrderPrintInfo orderPrintInfo = generateOrderPrintInfo(orderId, order.getStorageId());
                PlatformOrder platformOrder = getPlatformOrderById(orderId);
                List<PlatformOrderItem> list = getPlatformOrderItemByOrderId(orderId);
                platformOrder.setPlatformOrderItemList(list);
                PlatformOrders.add(platformOrder);
                orderPrintInfoList.add(orderPrintInfo);
            }
            new JsonResult(JsonResult.SUCCESS).addData("orderPrintInfoList", orderPrintInfoList).addData("orderDetailList", PlatformOrders).toJson(response);
        } catch (Exception e) {
            logger.error("获取订单详情失败", e);
            new JsonResult(JsonResult.FAILURE, "获取订单数据出错").toJson(response);
        }

    }


    /**
     * 根据物流单号查询订单商品信息 ,验货页面
     *
     * @param waybillNumber
     * @return
     */
    @RequestMapping(value = "/inspection")
    public void orderAndProductDetail(String waybillNumber, HttpServletResponse response) throws IOException {
        try {
            List<Long> orderIds = tradeCenterSupplierClient.getOrderIdsByExpressNo(waybillNumber);
            if (orderIds.size() > 0) {
                List<PlatformOrder> platformOrders = new ArrayList<PlatformOrder>();
                List<PlatformOrderItem> prodItemList = new ArrayList<PlatformOrderItem>();
                StringBuffer msg = new StringBuffer("订单");
                boolean isBack = false;
                for (long orderId : orderIds) {
                    Order order = tradeCenterSupplierClient.getSimpleOrder(orderId);

                    //验货界面只查询已经打印的，其它状态的不在展示中
                    if (order.getOrderState() != OrderState.Print) {
                        continue;
                    }
                    PlatformOrder platformOrder = getPlatformOrderById(orderId);

                    //设置订单编号
                    List<PlatformOrderItem> productSkuList = getPlatformOrderItemByOrderId(orderId);
                    for (PlatformOrderItem item : productSkuList) {
                        item.setOrderNo(platformOrder.getOrderNo());
                    }
                    //所有的订单项 统一放置处理
                    prodItemList.addAll(productSkuList);

                    platformOrders.add(platformOrder);
                    //查询该订单里面是否有退货记录,回去审核通过的退货记录的时间
                    BackGoodsLog backGoodsLog = tradeCenterSupplierClient.queryBackGoodsLogByState(orderId);
                    //判断该退货记录审核通过时间是否大于打印时间，如果大于不允许退货，重新打印
                    if (backGoodsLog != null && platformOrder.getOrderState().equals(OrderState.Print.toDesc())) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (backGoodsLog.getOperaTime().compareTo(format.parse(platformOrder.getPrintTime())) > 0) {
                            isBack = true;
                            msg.append(order.getOrderNo() + ",");
                        }
                    }
                }
                JsonResult result = new JsonResult(JsonResult.SUCCESS);
                if (isBack) {
                    msg.append("在完成打印之后有退货记录,最好将订单返回待处理,重新打印发货单及确认");
                    result.setMsg(msg.toString());
                }
                result.addData("waybillNumber", waybillNumber).addData(JsonResult.RESULT_TYPE_LIST, platformOrders).addData("itemList", prodItemList).toJson(response);
                return;
            }

        } catch (Exception e) {
            logger.error("获取订单详情失败", e);
        }
        new JsonResult(JsonResult.FAILURE, "根据物流号查找不到订单").toJson(response);
    }


    /**
     * 修改订单状态,返回上一级状态，确认发货功能
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public void updateOrderStatusByOrderId(int[] orderIds, int status, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            SupplierLog supplierLog = new SupplierLog();
            supplierLog.setIp(request.getRemoteAddr());
            supplierLog.setOperator(supplierAccount.getAccountName());
            /*判断是否为返回上一级状态功能，如果是已打印状态就修改为打出来状态，为待发货状态就修改为已打印 */
            if (status == 0) {   //返回上一级状态功能处理
                goBack(orderIds, supplierAccount, supplierLog);
            } else if (status == 1) {  //确认发货功能
                confirmDeliver(orderIds, supplierAccount, supplierLog);
            } else if (status == 2) {  //跳过验货的条形码验证 直接验货通过功能
                batchValidate(orderIds, supplierAccount, supplierLog);
            } else if (status == 3) {  //修改订单状态为已打印
                //验证订单中是否有没有设置物流信息的
                for (int id : orderIds) {
                    Logistics logistics = tradeCenterSupplierClient.getLogisticsByOrderId(id);
                    if (logistics == null || StringUtils.isEmpty(logistics.getDeliveryInfo().getWaybillNumber())) {
                        new JsonResult(JsonResult.FAILURE, "订单操作失败,获取不到订单的物流信息，订单号为：" + id).toJson(response);
                        return;
                    }
                }
                updateOrderStatusAffirmPrint(orderIds, supplierAccount, supplierLog);
            }
            createSupplierLog(supplierLog, supplierAccount.getCustomerId());
        } catch (OrderTransactionalException e) {
            new JsonResult(JsonResult.FAILURE, e.getMessage()).toJson(response);
            return;
        } catch (Exception e) {
            logger.error("商家系统修改订单状态出错", e);
            new JsonResult(JsonResult.FAILURE, "商家系统修改订单状态出错").toJson(response);
            return;
        }
        new JsonResult(JsonResult.SUCCESS).toJson(response);
    }


    /**
     * 修改订单状态为已打印
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/updateOrderStatusAffirmPrint", method = RequestMethod.POST)
    public void updateOrderStatusAffirmPrint(int[] orderIds, SupplierAccount supplierAccount, SupplierLog supplierLog) throws OrderBaseException {
        String orderNos = "";
        for (long orderId : orderIds) {
            tradeCenterSupplierClient.printOrderForSupplier(orderId, supplierAccount.getAccountName());
            Order order = tradeCenterSupplierClient.getSimpleOrder(orderId);
            orderNos = orderNos + String.valueOf(order.getOrderNo()) + ",";
        }
        if (!orderNos.equals("")) {
            supplierLog.setContent("打印操作，成功修改了订单" + orderNos + "的状态为已打印");
            supplierLog.setTitle("订单操作");
        }
    }


    /**
     * 验货
     *
     * @param orderIds
     * @param supplierAccount
     * @param supplierLog
     * @throws OrderBaseException
     */
    private void batchValidate(int[] orderIds, SupplierAccount supplierAccount, SupplierLog supplierLog) throws OrderBaseException {
        String orderNos = "";
        for (long orderId : orderIds) {
            logger.info("验货的orderI:" + orderId);
            Order order = tradeCenterSupplierClient.getSimpleOrder(orderId);
            orderNos = orderNos + order.getOrderNo() + ",";
            tradeCenterSupplierClient.validateOrderForSupplier(orderId, supplierAccount.getAccountName());
        }
        supplierLog.setContent("验货操作，成功修改了订单" + orderNos + "状态为待发货");
        supplierLog.setTitle("订单状态操作");
    }


    /**
     * 确认发货
     *
     * @param orderIds
     * @param supplierAccount
     * @param supplierLog
     * @throws OrderBaseException
     */
    private void confirmDeliver(int[] orderIds, SupplierAccount supplierAccount, SupplierLog supplierLog) throws OrderBaseException {
        String orderNos = "";
        for (int orderId : orderIds) {
            //确认发货 把订单状态修改为已发货
            tradeCenterSupplierClient.deliveryOrderForSupplier(orderId, supplierAccount.getAccountName());
            PlatformOrder order = getPlatformOrderById(orderId);
            if (!developMode) {
                //往第三方平台进行物流跟踪
                tradeCenterSupplierClient.queryThirdLogisticsInfo(order.getDeliveryTypePY(), order.getWaybillNumber(), "", order.getProvince());
                //短信功能
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", order.getName());
                params.put("orderNo", String.valueOf(order.getOrderNo()));
                params.put("expressCompany", order.getDeliveryType());
                params.put("expressNo", order.getWaybillNumber());
                messageTaskService.sendSmsMessage(order.getMobile(), params, MessageTemplateName.DELIVER);
            }
            orderNos = orderNos + order.getOrderNo() + ",";
        }
        supplierLog.setContent("成功修改了订单" + orderNos + "的状态为已发货");
        supplierLog.setTitle("订单状态操作");
    }


    /**
     * 返回上一级状态操作
     *
     * @param orderIds
     * @param supplierAccount
     * @return
     * @throws OrderBaseException
     */
    private SupplierLog goBack(int[] orderIds, SupplierAccount supplierAccount, SupplierLog supplierLog) throws OrderBaseException {
        String orderState = "";
        String orderNos = "";
        for (long orderId : orderIds) {
            Order order = tradeCenterSupplierClient.getSimpleOrder(orderId);
            orderState = order.getOrderState().supplierDesc();
            if (order.getOrderState() == OrderState.Print) {
                tradeCenterSupplierClient.turnBackOrderToConfirmForSupplier(orderId, supplierAccount.getAccountName());
            } else {
                tradeCenterSupplierClient.turnBackOrderToPrintForSupplier(orderId, supplierAccount.getAccountName());
            }
            orderNos = orderNos + order.getOrderNo() + ",";
        }
        supplierLog.setContent("成功修改了订单" + orderNos + "的" + orderState + "状态返回到了上一级");
        supplierLog.setTitle("订单返回上一级状态");
        return supplierLog;
    }

    @RequestMapping("/excel-{date}")
    public String exportExcel(Query query, HttpServletRequest request, Model model) {
        if(query== null){
            query = new Query();
        }
        if (query.getOrderState() == null) {
            query.setOrderState(OrderState.Confirm);
        } else if (query.getOrderState() == OrderState.Success) {
            query.setOrderState(null);
        }
        List<PlatformOrderItem> platformOrderItems = new ArrayList<PlatformOrderItem>();
        Map<Long, PlatformOrder> platformOrderMap = new HashMap<Long, PlatformOrder>();
        try {
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            query.setCustomerId(supplierAccount.getCustomerId());
            List<PlatformOrder> platformOrders = getPlatformOrderByQuery(query);
            if(platformOrders != null && !platformOrders.isEmpty()) {
                for(PlatformOrder platformOrder : platformOrders) {
                    platformOrderItems.addAll(super.getPlatformOrderItemByOrderId(platformOrder.getId()));
                    platformOrderMap.put(platformOrder.getId(), platformOrder);
                }
            }
        } catch (Exception e) {
            logger.error("获取订单数据出错", e);
        }
        logger.info("platformOrderItems size:" + platformOrderItems.size());
        model.addAttribute("platformOrderItems", platformOrderItems);
        model.addAttribute("platformOrderMap", platformOrderMap);
        return "excelView";
    }


    public void setDevelopMode(boolean developMode) {
        this.developMode = developMode;
    }
}

