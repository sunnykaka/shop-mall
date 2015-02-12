package com.kariqu.tradesystem.web;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.login.SessionUtils;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.client.TradeCenterBossClient;
import com.kariqu.tradecenter.domain.InvoiceInfo;
import com.kariqu.tradecenter.domain.Logistics;
import com.kariqu.tradecenter.domain.LogisticsRedundancy;
import com.kariqu.tradecenter.domain.OrderMessage;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.tradesystem.helper.AddressInfo;
import com.kariqu.tradesystem.helper.InvoiceVoInfo;
import com.kariqu.usercenter.service.MessageTaskService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台订单修改控制器
 * 审核订单，客服可修改订单的收货地址信息和发票信息
 * <p/>
 * 目前收货地址的省市区是在数据库中用一个字段保存的，所以引入了复杂的界面逻辑来保存这个字段
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-12-11
 *        Time: 下午3:09
 */
@Controller
public class OrderChangeController {

    private final Log logger = LogFactory.getLog(OrderChangeController.class);

    @Autowired
    private TradeCenterBossClient tradeCenterBossClient;

    @Autowired
    private OrderQueryService orderQueryService;


    @Autowired
    private MessageTaskService messageTaskService;


    public static final String Split_New_Gap = "\\^";


    /**
     * 修改发货人信息
     *
     * @param addressInfo
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/order/update/addressInfo", method = RequestMethod.POST)
    @Permission("客服修改了客户订单的地址信息")
    public void updateConsigneeInfo(AddressInfo addressInfo, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Account currentAccount = SessionUtils.getLoginAccount(request.getSession());
        try {
            Logistics logistics = orderQueryService.getLogisticsByOrderId(addressInfo.getOrderId());
            LogisticsRedundancy logisticsRedundancy = new LogisticsRedundancy();
            logisticsRedundancy.setEditor(currentAccount.getUserName());
            logisticsRedundancy.setId(logistics.getId());

            String mobile = addressInfo.getMobile();
            if (StringUtils.isNotEmpty(mobile)) {
                String[] mobileSplit = mobile.split(Split_New_Gap);
                logisticsRedundancy.setMobileRewrite(mobileSplit[mobileSplit.length - 1]);
            }
            String consignee = addressInfo.getConsignee();
            if (StringUtils.isNotEmpty(consignee)) {
                String[] consigneeSplit = consignee.split(Split_New_Gap);
                logisticsRedundancy.setNameRewrite(consigneeSplit[consigneeSplit.length - 1]);
            }

            String zipCode = addressInfo.getZipCode();
            if (StringUtils.isNotEmpty(zipCode)) {
                String[] zipCodeSplit = zipCode.split(Split_New_Gap);
                logisticsRedundancy.setZipCodeRewrite(zipCodeSplit[zipCodeSplit.length - 1]);
            }

            String location = addressInfo.getLocation();
            if (StringUtils.isNotEmpty(location)) {
                String[] locationSplit = location.split(Split_New_Gap);
                logisticsRedundancy.setLocationRewrite(locationSplit[locationSplit.length - 1]);
            }

            //将省市区串起来保存
            String province = addressInfo.getProvince();
            String city = addressInfo.getCity();
            String districts = addressInfo.getDistricts();

            if (StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city) && StringUtils.isNotEmpty(districts)) {
                String[] provinceSplit = province.split(Split_New_Gap);
                String[] citySplit = city.split(Split_New_Gap);
                String[] districtsSplit = districts.split(Split_New_Gap);
                logisticsRedundancy.setProvinceRewrite(provinceSplit[provinceSplit.length - 1] + "," + citySplit[citySplit.length - 1] + "," + districtsSplit[districtsSplit.length - 1]);
            }
            tradeCenterBossClient.updateLogisticsRedundancy(logisticsRedundancy);
        } catch (Exception e) {
            logger.error("订单管理的修改发货人信息异常：" + e);
            new JsonResult(false, "修改发货人信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 修改发票信息
     *
     * @param formData
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/update/invoiceInfo", method = RequestMethod.POST)
    @Permission("客服修改了客户订单的发票信息")
    public void updateInvoiceInfo(InvoiceVoInfo formData, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Account currentAccount = SessionUtils.getLoginAccount(request.getSession());
        try {
            InvoiceInfo invoiceInfo = new InvoiceInfo();
            String companyName = formData.getCompanyName();
            String[] companySplit = companyName.split(Split_New_Gap);
            invoiceInfo.setCompanyNameRewrite(companySplit[companySplit.length - 1]);

            String invoiceContent = formData.getInvoiceContent();
            String[] contentSplit = invoiceContent.split(Split_New_Gap);
            invoiceInfo.setInvoiceContentRewrite(contentSplit[contentSplit.length - 1]);

            String invoiceTitle = formData.getInvoiceTitle();
            String[] titleSplit = invoiceTitle.split(Split_New_Gap);
            String newTitle = titleSplit[titleSplit.length - 1];

            String invoiceType = formData.getInvoiceType();
            String[] typeSplit = invoiceType.split(Split_New_Gap);
            invoiceInfo.setInvoiceTypeRewrite(typeSplit[typeSplit.length - 1]);


            if (newTitle.equals("公司")) {
                invoiceInfo.setInvoiceTitleRewrite(InvoiceInfo.InvoiceTitle.company);
            } else {
                invoiceInfo.setInvoiceTitleRewrite(InvoiceInfo.InvoiceTitle.individual);
            }

            invoiceInfo.setEditor(currentAccount.getUserName());
            tradeCenterBossClient.updateOrderInvoiceInfoRedundancy(formData.getOrderId(), invoiceInfo);
        } catch (Exception e) {
            logger.error("订单管理的修改发票信息异常：" + e);
            new JsonResult(false, "修改发票信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 客服修改订单的快递公司
     *
     * @param orderId
     * @param deliveryType
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/delivery/update", method = RequestMethod.POST)
    @Permission("客服修改了订单的快递公司")
    public void changeDelivery(long orderId, DeliveryInfo.DeliveryType deliveryType, HttpServletResponse response) throws IOException {
        try {
            tradeCenterBossClient.updateOrderDeliveryType(orderId, deliveryType);
        } catch (Exception e) {
            logger.error("客服修改快递公司异常：" + e);
            new JsonResult(false, "客服修改快递公司出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/order/waybillNumber/update", method = RequestMethod.POST)
    @Permission("客服修改了订单的物流编号")
    public void changeDelivery(long orderId, String waybillNumber, HttpServletResponse response) throws IOException {
        try {
            tradeCenterBossClient.updateWaybillNumberByOrderId(orderId, waybillNumber);
        } catch (Exception e) {
            logger.error("客服修改订单(" + orderId + ")物流编号(" + waybillNumber + ")异常：" + e);
            new JsonResult(false, "客服修改物流编号出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 客服确认订单，订单被确认后进入商家系统
     *
     * @param orderId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/state/confirm", method = RequestMethod.POST)
    @Permission("客服确认了订单")
    public void confirmOrderState(long orderId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Account currentAccount = SessionUtils.getLoginAccount(request.getSession());

        try {
            tradeCenterBossClient.approvalOrderOfPaySuccess(orderId, currentAccount.getUserName());
        } catch (OrderBaseException ex) {
            new JsonResult(false, ex.getMessage()).toJson(response);
            return;
        } catch (Exception e) {
            logger.error("客服确认订单异常：" + e);
            new JsonResult(false, "客服确认订单异常出错").toJson(response);
            return;
        }
        Logistics logistics = orderQueryService.getLogisticsByOrderId(orderId);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userName", logistics.getName());
        //messageTaskService.sendSmsMessage(logistics.getMobile(), params, MessageTemplateName.ORDER_CONFIRM);
        new JsonResult(true).toJson(response);
    }

    /**
     * 客服添加备注
     *
     * @param orderMessage
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/comments/add", method = RequestMethod.POST)
    @Permission("客服添加了订单备注")
    public void addOrderMessage(OrderMessage orderMessage, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Account currentAccount = SessionUtils.getLoginAccount(request.getSession());
        try {
            orderMessage.setUserId(currentAccount.getId());
            orderMessage.setUserName(currentAccount.getUserName());
            orderMessage.setUserType(OrderMessage.UserType.Server);
            tradeCenterBossClient.appendOrderMessage(orderMessage);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("客服添加订单备注发生异常：" + e);
            new JsonResult(false, "客服添加订单备注出错").toJson(response);
            return;
        }
    }

}
