package com.kariqu.tradesystem.web;

import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.suppliercenter.domain.Brand;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.tradecenter.client.TradeCenterBossClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.service.AddressService;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.tradecenter.service.OrderQuery;
import com.kariqu.tradesystem.helper.*;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * User: Wendy
 * Date: 12-6-20
 * Time: 上午9:31
 */

@Controller
public class OrderDisplayController {

    public static final String Old_New_Gap = "^";

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TradeCenterBossClient tradeCenterBossClient;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AddressService addressService;

    /**
     * 查询等待客服确认的订单数量
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/confirm/count")
    public void waitConfirmCount(HttpServletResponse response) throws IOException {
        new JsonResult(true).addData("orderCount", tradeCenterBossClient.queryCountOfOrderWaitForApproval()).toJson(response);
    }


    /**
     * 后台订单的筛选地址
     *
     * @param orderQuery
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/search")
    public void searchOrder(OrderQuery orderQuery, HttpServletResponse response) throws IOException {
        List<OrderDetail> list = new ArrayList<OrderDetail>();
        Page<Order> orderPage = tradeCenterBossClient.searchOrderByQuery(orderQuery);
        for (Order order : orderPage.getResult()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setUserId(order.getUserId());
            orderDetail.setUserName(order.getUserName());
            orderDetail.setOrderId(order.getId());
            orderDetail.setOrderNo(order.getOrderNo());
            orderDetail.setAccountType(order.getAccountType());
            if (order.getOrderState() == OrderState.Success) {
                orderDetail.setEndDate(DateUtils.formatDate(order.getEndDate(), DateUtils.DateFormatType.DATE_FORMAT_STR));
            }
            orderDetail.setStartDate(DateUtils.formatDate(order.getCreateDate(), DateUtils.DateFormatType.DATE_FORMAT_STR));
            orderDetail.setInvoice(order.getInvoiceInfo().isInvoice());
            orderDetail.setOrderState(order.getOrderState().toString());
            orderDetail.setOrderStateDesc(order.getOrderState().serviceDesc());

            Logistics logistics = tradeCenterBossClient.queryLogisticsByOrderId(order.getId());
            orderDetail.setGoodToUserName(logistics.getName());


            list.add(orderDetail);
        }
        new JsonResult(true).addData("totalCount", orderPage.getTotalCount()).addData("result", list).toJson(response);
    }

    @RequestMapping(value = "/order/export")
    public void exportOrder(OrderQuery orderQuery, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<List<Order>> orderList = tradeCenterBossClient.searchOrderListByQuery(orderQuery);

        String name = "订单统计";
        Map<String, List> exportOrderMap = new LinkedHashMap<String, List>();
        for (List<Order> orderArray : orderList) {
            if (orderArray == null || orderArray.size() == 0) continue;

            List<ExportOrder> exportOrderList = new ArrayList<ExportOrder>();
            for (Order order : orderArray) {
                long orderId = order.getId();
                Logistics logistics = order.getLogistics();

                int count = 0;
                for (OrderItem orderItem : order.getOrderItemList()) {
                    ExportOrder exportOrder = new ExportOrder();

                    Brand brand = supplierService.queryBrandById(orderItem.getBrandId());
                    exportOrder.setBrandName(brand == null ? "" : brand.getName());
                    exportOrder.setProductName(orderItem.getSkuName());
                    exportOrder.setProductCode(orderItem.getItemNo());
                    exportOrder.setNumber(String.valueOf(orderItem.getNumber()));
                    exportOrder.setUnitPrice(Money.getMoneyString(orderItem.getUnitPrice()));
                    exportOrder.setBackNumber(String.valueOf(orderItem.getBackNum()));
                    exportOrder.setOrderState(orderItem.getOrderState().serviceDesc());

                    if (count == 0) {
                        exportOrder.setPayBank(order.getPayBank().toDesc());
                        exportOrder.setCreateDate(formatDate(order.getCreateDate()));
                        exportOrder.setSuccessDate(formatDate(tradeCenterBossClient.querySuccessDate(orderId)));
                        exportOrder.setUserName(order.getUserName());
                        exportOrder.setOrderNo(String.valueOf(order.getOrderNo()));

                        exportOrder.setOldTotalPrice(Money.getMoneyString(order.calculateOldTotalPrice()));
                        exportOrder.setPriceMessageDetail(order.getPriceMessageDetail());
                        exportOrder.setTotalPrice(order.getTotalPrice());
                        exportOrder.setRemark(remark(order));
                        exportOrder.setLogisticsName(logistics.getName());
                        exportOrder.setLogisticsAddress(logistics.getProvince() + "," + logistics.getLocation());
                        exportOrder.setLogisticsPhone(getPhone(logistics.getMobile(), logistics.getTelephone()));
                    }

                    // 只存到最后一个订单项里, 主要用来多条订单项时合并单元格
                    if (count == (order.getOrderItemList().size() - 1)) {
                        exportOrder.setItemSize(order.getOrderItemList().size());
                    }
                    exportOrderList.add(exportOrder);
                    count++;
                }
            }
            String sheetName = name;
            if (orderArray.size() > 0 && orderArray.get(0).getOrderState() == orderArray.get(orderArray.size() - 1).getOrderState())
                sheetName = orderArray.get(0).getOrderState().serviceDesc();
            exportOrderMap.put(sheetName, exportOrderList);
        }

        String[] headerList = new String[]{"下单时间", "完成时间", "下单人", "订单编号", "品牌", "商品名称",
                "商品编码", "购买数量", "购买单价", "应付总金额", "使用积分/优惠券", "实付总金额",
                "退货数量", "订单状态", "备注(发票、留言)", "收货人", "收货人地址", "联系电话"};
        String[] fieldNameList = new String[]{"createDate", "successDate", "userName", "orderNo", "brandName", "productName",
                "productCode", "number", "unitPrice", "oldTotalPrice", "priceMessageDetail", "totalPrice",
                "backNumber", "orderState", "remark", "logisticsName", "logisticsAddress", "logisticsPhone"};

        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFileName(request, name) + ".xls\"");
        response.setContentType("application/vnd.ms-excel");
        ExportExcelUtil.exportExcel(exportOrderMap, headerList, fieldNameList, response.getOutputStream());
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

    private String formatDate(Date date) {
        return DateUtils.formatDate(date, DateUtils.DateFormatType.DATE_FORMAT_STR);
    }

    private String remark(Order order) {
        StringBuilder sbd = new StringBuilder();
        String invoice = invoice(order);
        String message = message(order.getId());

        sbd.append(invoice);
        if (StringUtils.isNotBlank(invoice) && StringUtils.isNotBlank(message))
            sbd.append("\n");
        sbd.append(message);

        return sbd.toString();
    }

    private String invoice(Order order) {
        // 不需要开发票
        if (!order.getInvoiceInfo().isInvoice()) return "";

        StringBuilder sbd = new StringBuilder("发票: (");
        sbd.append(order.getInvoiceInfo().getInvoiceType()).append("/");
        sbd.append(order.getInvoiceInfo().getInvoiceTitle().toDesc()).append("/");

        if (StringUtils.isNotBlank(order.getInvoiceInfo().getCompanyName()))
            sbd.append(order.getInvoiceInfo().getCompanyName()).append("/");

        sbd.append(order.getInvoiceInfo().getInvoiceContent()).append(")");
        return sbd.toString();
    }

    private String message(long orderId) {
        StringBuilder sbd = new StringBuilder();
        List<OrderMessage> orderMessages = tradeCenterBossClient.queryAllMessage(orderId);
        int i = 0;
        for (OrderMessage orderMessage : orderMessages) {
            sbd.append(orderMessage.getUserType().toDesc()).append("留言:").append(orderMessage.getMessageInfo());
            if (i != orderMessages.size() - 1)
                sbd.append("\n");

            i++;
        }
        return sbd.toString();
    }

    private String getPhone(String mobile, String telephone) {
        if (StringUtils.isBlank(mobile))
            return telephone;
        if (StringUtils.isBlank(telephone))
            return mobile;
        return mobile + "/" + telephone;
    }

    /**
     * 根据订单Id查询Sku列表
     *
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/order/sku/{orderId}")
    public void skuGrid(@PathVariable("orderId") int orderId, HttpServletResponse response) throws IOException {
        List<OrderItem> orderItems = tradeCenterBossClient.queryOrderItemWithoutBackingNumberByOrderId(orderId);
        List<ProductSku> list = new ArrayList<ProductSku>();
        for (OrderItem orderItem : orderItems) {
            ProductSku sku = new ProductSku();
            sku.setNumber(orderItem.getNumber());
            sku.setSkuId(orderItem.getSkuId());
            sku.setProductId(orderItem.getProductId());
            sku.setSkuState(orderItem.getOrderState().serviceDesc());
            sku.setShipmentNum(orderItem.getShipmentNum());
            sku.setBackNumber(orderItem.getBackNum());
            sku.setBarCode(orderItem.getBarCode());
            StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnit(orderItem.getSkuId());
            if (stockKeepingUnit != null) {
                Product product = productService.getProductById(stockKeepingUnit.getProductId());
                Money money = new Money();
                money.setCent(stockKeepingUnit.getPrice());
                sku.setSkuPrice(money.toString());
                sku.setProductName(product.getName());
                sku.setAttribute(skuService.getSkuPropertyToString(stockKeepingUnit));
                sku.setProductCode(stockKeepingUnit.getSkuCode());//product.getProductCode());
            } else {
                sku.setProductName("没有此 SKU! 被删除或重新生成过.");
            }

            list.add(sku);
        }
        new JsonResult(true).addData("totalCount", list.size()).addData("result", list).toJson(response);
    }

    @RequestMapping(value = "/order/logistics/{orderId}")
    public void logisticsInfo(@PathVariable("orderId") String orderId, HttpServletResponse response) throws IOException {
        List<ProgressDetail> list = tradeCenterBossClient.getProgressDetail(NumberUtils.toLong(orderId));
        new JsonResult(true).addData("totalCount", list.size()).addData("result", list).toJson(response);
    }


    /**
     * 显示订单的收货信息，客服可修改，修改的和原来的用-隔开
     *
     * @param orderId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/addressInfo/{orderId}")
    public void addressInfoGrid(@PathVariable("orderId") long orderId, HttpServletResponse response) throws IOException {
        AddressInfo addressInfo = new AddressInfo();
        Logistics logistics = tradeCenterBossClient.queryLogisticsByOrderId(orderId);
        LogisticsRedundancy logisticsRedundancy = tradeCenterBossClient.queryLogisticsRedundancy(logistics.getId());
        Order order = tradeCenterBossClient.queryOrderById(orderId);
        List<AddressInfo> list = new LinkedList<AddressInfo>();
        if (order != null) {
            addressInfo.setOrderState(order.getOrderState().toString());
            String[] province = (logistics.getProvince()).split(",");
            if (province.length == 2) {
                addressInfo.setProvince(province[0]);
                addressInfo.setCity(province[0]);
                addressInfo.setDistricts(province[1]);
            } else {
                addressInfo.setProvince(province[0]);
                addressInfo.setCity(province[1]);
                addressInfo.setDistricts(province[2]);
            }
            addressInfo.setConsignee(logistics.getName());
            addressInfo.setLocation(logistics.getLocation());
            addressInfo.setMobile(logistics.getMobile());
            addressInfo.setZipCode(logistics.getZipCode());

            if (StringUtils.isNotEmpty(logisticsRedundancy.getNameRewrite())) {
                addressInfo.setConsignee(addressInfo.getConsignee() + Old_New_Gap + logisticsRedundancy.getNameRewrite());
            }
            if (StringUtils.isNotEmpty(logisticsRedundancy.getLocationRewrite())) {
                addressInfo.setLocation(addressInfo.getLocation() + Old_New_Gap + logisticsRedundancy.getLocationRewrite());
            }
            if (StringUtils.isNotEmpty(logisticsRedundancy.getMobileRewrite())) {
                addressInfo.setMobile(addressInfo.getMobile() + Old_New_Gap + logisticsRedundancy.getMobileRewrite());
            }
            if (StringUtils.isNotEmpty(logisticsRedundancy.getZipCodeRewrite())) {
                addressInfo.setZipCode(addressInfo.getZipCode() + Old_New_Gap + logisticsRedundancy.getZipCodeRewrite());
            }

            String provinceRewrite = logisticsRedundancy.getProvinceRewrite();
            if (StringUtils.isNotEmpty(provinceRewrite)) {
                String[] newProvince = (logisticsRedundancy.getProvinceRewrite()).split(",");
                addressInfo.setProvince(addressInfo.getProvince() + Old_New_Gap + newProvince[0]);
                addressInfo.setCity(addressInfo.getCity() + Old_New_Gap + newProvince[1]);
                addressInfo.setDistricts(addressInfo.getDistricts() + Old_New_Gap + newProvince[2]);
            }
            list.add(addressInfo);
        }
        new JsonResult(true).addData("totalCount", 1).addData("result", list).toJson(response);
    }

    /**
     * 配送和支付信息
     *
     * @param orderId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/pay/delivery/{orderId}")
    public void payDeliveryInfoGrid(@PathVariable("orderId") long orderId, HttpServletResponse response) throws IOException {
        Order order = tradeCenterBossClient.queryOrderById(orderId);
        List<PayDeliveryInfo> list = new LinkedList<PayDeliveryInfo>();
        if (order != null) {
            Logistics logistics = tradeCenterBossClient.queryLogisticsByOrderId(order.getId());
            PayDeliveryInfo payDeliveryInfo = new PayDeliveryInfo();
            payDeliveryInfo.setPayType(order.getPayType());
            payDeliveryInfo.setTotalPrice(order.getTotalPrice());
            payDeliveryInfo.setDeliveryType(logistics.getDeliveryInfo().getDeliveryType());
            payDeliveryInfo.setPayBank(order.getPayBank().toDesc());
            payDeliveryInfo.setOrderState(order.getOrderState().toString());
            payDeliveryInfo.setWaybillNumber(logistics.getDeliveryInfo().getWaybillNumber());
            list.add(payDeliveryInfo);
        }
        new JsonResult(true).addData("totalCount", 1).addData("result", list).toJson(response);
    }


    /**
     * 显示订单的发票信息,客服也可修改，修改后的和原来的用-隔开
     *
     * @param orderId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/invoice/{orderId}")
    public void invoiceInfoGrid(@PathVariable("orderId") long orderId, HttpServletResponse response) throws IOException {
        Order order = tradeCenterBossClient.queryOrderById(orderId);
        List<InvoiceVoInfo> list = new LinkedList<InvoiceVoInfo>();
        if (order != null) {
            InvoiceInfo change = tradeCenterBossClient.queryOrderInvoiceInfoRedundancy(orderId);
            InvoiceVoInfo invoiceInfo = new InvoiceVoInfo();
            invoiceInfo.setInvoiceContent(order.getInvoiceInfo().getInvoiceContent());
            invoiceInfo.setInvoiceTitle(order.getInvoiceInfo().getInvoiceTitle().toDesc());
            invoiceInfo.setInvoiceType(order.getInvoiceInfo().getInvoiceType());
            invoiceInfo.setCompanyName(order.getInvoiceInfo().getCompanyName());

            if (StringUtils.isNotEmpty(change.getInvoiceContentRewrite())) {
                invoiceInfo.setInvoiceContent(invoiceInfo.getInvoiceContent() + Old_New_Gap + change.getInvoiceContentRewrite());
            }
            if (change.getInvoiceTitleRewrite() != null) {
                invoiceInfo.setInvoiceTitle(invoiceInfo.getInvoiceTitle() + Old_New_Gap + change.getInvoiceTitleRewrite().toDesc());
            }
            if (StringUtils.isNotEmpty(change.getInvoiceTypeRewrite())) {
                invoiceInfo.setInvoiceType(invoiceInfo.getInvoiceType() + Old_New_Gap + change.getInvoiceTypeRewrite());
            }
            if (StringUtils.isNotEmpty(change.getCompanyNameRewrite())) {
                String companyName = invoiceInfo.getCompanyName();
                if (StringUtils.isNotEmpty(companyName)) {
                    invoiceInfo.setCompanyName(companyName + Old_New_Gap + change.getCompanyNameRewrite());
                } else {
                    invoiceInfo.setCompanyName("不开公司发票^" + change.getCompanyNameRewrite());
                }
            }
            invoiceInfo.setOrderState(order.getOrderState().toString());
            list.add(invoiceInfo);
        }
        new JsonResult(true).addData("totalCount", 1).addData("result", list).toJson(response);
    }


    /**
     * 备注列表
     *
     * @param orderId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/message/{orderId}")
    public void displayOrderMessage(@PathVariable("orderId") long orderId, HttpServletResponse response) throws IOException {
        List<OrderMessage> orderMessages = tradeCenterBossClient.queryAllMessage(orderId);
        for (OrderMessage orderMessage : orderMessages) {
            orderMessage.setMessageInfo(orderMessage.getUserType().toDesc() + ":" + orderMessage.getMessageInfo());
        }
        new JsonResult(true).addData("totalCount", orderMessages.size()).addData("result", orderMessages).toJson(response);
    }


    /**
     * 订单价格备注详情
     *
     * @param orderId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/order/priceMessageDetail/{orderId}")
    public void priceMessageDetailByOrderId(@PathVariable("orderId") long orderId, HttpServletResponse response) throws IOException {
        OrderDetail orderDetail = new OrderDetail();
        List<OrderDetail> list = new LinkedList<OrderDetail>();
        Order order = tradeCenterBossClient.queryOrderById(orderId);
        if (order.getPriceMessageDetail() != null) {
            orderDetail.setPriceMessageDetail(order.getPriceMessageDetail());
            list.add(orderDetail);
        }
        new JsonResult(true).addData("totalCount", list.size()).addData("result", list).toJson(response);
    }

    @RequestMapping(value = "/order/search/integral")
    public void searchUserIntegralByOrder(String orderNoes, HttpServletResponse response) throws IOException {
        String[] orderNoArray = orderNoes.split("\\n");

        Map<Integer, UserIntegralAndCoupon> uicMap = new LinkedHashMap<Integer, UserIntegralAndCoupon>();
        StringBuilder sbd = new StringBuilder();
        for (String orderNo : orderNoArray) {
            if (StringUtils.isBlank(orderNo) || NumberUtils.toLong(orderNo) <= 0) continue;

            long no = NumberUtils.toLong(orderNo.trim());
            Order order = tradeCenterBossClient.queryOrderByOrderNo(no);
            if (order == null) continue;

            User user = userService.getUserById(order.getUserId());
            if (user == null || uicMap.get(user.getId()) != null) continue;

            UserIntegralAndCoupon uic = new UserIntegralAndCoupon();
            uic.setUserId(user.getId());
            uic.setUserName(user.getUserName());
            uic.setPhone(StringUtils.isNotBlank(user.getPhone()) ? user.getPhone() : "");
            uic.setEmail(StringUtils.isNotBlank(user.getEmail()) ? user.getEmail() : "");
            uic.setIntegral(user.getCurrency());

            List<Coupon> coupons = couponService.queryCouponByUserId(order.getUserId());
            sbd.delete(0, sbd.length());
            int i = 0;
            String str = "";
            for (Coupon coupon : coupons) {
                sbd.append(coupon.getCode());
                if (coupon.isUsed())
                    str = "已使用";
                else if (coupon.isExpire())
                    str = "已过期";
                if (StringUtils.isNotBlank(str))
                    sbd.append("(").append(str).append(")");

                if (i != coupons.size() - 1) sbd.append(", ");

                i++;
            }
            uic.setCoupon(sbd.toString());

            sbd.delete(0, sbd.length());
            // 从地址中去寻找
            List<Address> addresses = addressService.queryAllAddress(order.getUserId());
            i = 0;
            for (Address address : addresses) {
                sbd.append(address.getName()).append("/").append(address.getMobile()).append("/");
                sbd.append(address.getProvince()).append(address.getLocation());
                if (address.isDefaultAddress()) sbd.append("<span style='color:red;'>(默认地址)</span>");

                if (i != addresses.size() - 1) sbd.append("\n");
            }
            uic.setAddress(sbd.toString());

            uicMap.put(user.getId(), uic);
        }
        new JsonResult(true).addData("orderList", uicMap.values()).toJson(response);
    }

}
