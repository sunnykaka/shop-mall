package com.kariqu.suppliersystem.logistics.web;

import com.kariqu.common.DateUtils;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.LogisticsPrintInfo;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliersystem.common.JsonResult;
import com.kariqu.suppliersystem.orderManager.vo.OrderPrintInfo;
import com.kariqu.suppliersystem.orderManager.web.BaseController;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import com.kariqu.tradecenter.domain.Order;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: amos.zhou
 * Date: 13-9-28
 * Time: 下午4:03
 */
@Controller
@RequestMapping("/logistics")
public class LogisticsController extends BaseController {

    private final Log logger = LogFactory.getLog(LogisticsController.class);

    /**
     * 查询物流界面里的打印代码
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/print_html/{name}")
    public void queryLogisticsPrintInfoHtmlByName(@PathVariable("name") DeliveryInfo.DeliveryType name, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            LogisticsPrintInfo logisticsPrintInfo = logisticsPrintInfoService.queryLogisticsPrintInfoByNameAndCustomerId(name, supplierAccount.getCustomerId());

            if (logisticsPrintInfo == null || StringUtils.isBlank(logisticsPrintInfo.getPrintHtml())) {
                response.getWriter().write(StringUtils.EMPTY);
                return;
            }
            response.getWriter().write(logisticsPrintInfo.getPrintHtml());
        } catch (Exception e) {
            logger.error("商家管理获取物流信息出错",e);
            response.getWriter().write("error");
        }
    }

    /**
     * 查询物流信息,打印物流信息界面
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/print_info", method = RequestMethod.POST)
    public void queryLogisticsPrintInfo(@RequestParam("orderIds") long[] orderIds, HttpServletResponse response) throws IOException {
        try {
            List<OrderPrintInfo> orderPrintInfoList = new ArrayList<OrderPrintInfo>();
            for (long orderId : orderIds) {
                Order order = tradeCenterSupplierClient.getSimpleOrder(orderId);
                OrderPrintInfo orderPrintInfo = generateOrderPrintInfo(orderId, order.getStorageId());
                orderPrintInfo.setDeliveryTime(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
                orderPrintInfoList.add(orderPrintInfo);
            }
            new JsonResult(JsonResult.SUCCESS).addData("orderPrintInfoList", orderPrintInfoList).toJson(response);
        } catch (Exception e) {
            logger.error("商家管理获取物流信息出错",e);
            new JsonResult(JsonResult.FAILURE, "服务器出错").toJson(response);
        }
    }





}
