package com.kariqu.suppliersystem.orderManager.web;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.categorycenter.domain.service.ProductCategoryService;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.suppliercenter.domain.*;
import com.kariqu.suppliercenter.service.LogisticsPrintInfoService;
import com.kariqu.suppliercenter.service.SupplierLogService;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.suppliersystem.orderManager.vo.OrderGift;
import com.kariqu.suppliersystem.orderManager.vo.PlatformOrder;
import com.kariqu.suppliersystem.orderManager.vo.PlatformOrderItem;
import com.kariqu.tradecenter.client.TradeCenterSupplierClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.service.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-12-18
 * Time: 上午10:34
 */
public abstract class PlatformController {

    @Autowired
    protected SkuService skuService;

    @Autowired
    protected SkuStorageService skuStorageService;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected ProductCategoryService productCategoryService;

    @Autowired
    protected SupplierService supplierService;

    @Autowired
    protected CategoryPropertyService categoryPropertyService;

    @Autowired
    protected TradeCenterSupplierClient tradeCenterSupplierClient;

    @Autowired
    protected LogisticsPrintInfoService logisticsPrintInfoService;

    @Autowired
    protected SupplierLogService supplierLogService;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected List<PlatformOrder> getPlatformOrderByQuery(Query query) {
        List<PlatformOrder> platformOrderList=new ArrayList<PlatformOrder>();
       // Page<Order> orderPage = tradeCenterSupplierClient.searchQuery(query,query.getCustomerId(), new Page<Order>(0, query.getLimit()));
        List<Order> orderList = tradeCenterSupplierClient.searchQueryList(query, query.getCustomerId());
        for(Order order:orderList){
            platformOrderList.add(generatePlatOrder(order));
        }
        return platformOrderList;
    }


    protected PlatformOrder getPlatformOrderById(long Id) {
        return generatePlatOrder(tradeCenterSupplierClient.getSimpleOrder(Id));
    }

    protected List<PlatformOrderItem> getPlatformOrderItemByOrderId(long orderId) {
        List<PlatformOrderItem> platformOrderItemList=new ArrayList<PlatformOrderItem>();
        List<OrderItem> orderItems = tradeCenterSupplierClient.queryOrderItemWithoutBackingNumberByOrderId(orderId);
        getGiftItems(orderId, platformOrderItemList, orderItems);
        getOrderItems(orderId, platformOrderItemList, orderItems);
        return platformOrderItemList;
    }

    private void getOrderItems(long orderId, List<PlatformOrderItem> platformOrderItemList, List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnitWithStock(orderItem.getSkuId(), orderItem.getStorageId());
            if (orderItem.getShipmentNum() > 0) {
                PlatformOrderItem platformOrderItem=new PlatformOrderItem();
                ProductCategory productCategory = productCategoryService.getProductCategoryById(orderItem.getCategoryId());
                Brand brand = supplierService.queryBrandById(orderItem.getBrandId());
                String brandName = "";
                if (brand != null) {
                    brandName = brand.getName();
                }
                platformOrderItem.setOrderId(orderId);
                platformOrderItem.setBrandName(brandName);
                if (productCategory != null) {
                    platformOrderItem.setCategoryName(productCategory.getName());//类目名称，暂时没有
                }
                Money money = new Money();
                if (stockKeepingUnit != null) {
                    SkuStorage skustorage=skuStorageService.getSkuStorage(orderItem.getSkuId());
                    if(skustorage!=null){
                        platformOrderItem.setStockQuantity(skustorage.getStockQuantity());
                    }
                    money.setCent(stockKeepingUnit.getPrice());
                    platformOrderItem.setSkuAttribute(orderItem.getSkuExplain());
                    platformOrderItem.setSkuPrice(money.toString());
                    money.setCent(orderItem.getUnitPrice());
                    platformOrderItem.setUnitPrice(money.toString());
                } else {
                    platformOrderItem.setSkuAttribute("无");
                    platformOrderItem.setSkuPrice("0");
                }
                platformOrderItem.setId(orderItem.getId());
                platformOrderItem.setUnitPrice(orderItem.getUnitPriceByMoney());
                platformOrderItem.setProductName(orderItem.getSkuName());
                platformOrderItem.setItemNo(orderItem.getItemNo());
                platformOrderItem.setBarCode(orderItem.getBarCode());
                platformOrderItem.setNumber(orderItem.getNumber());
                platformOrderItem.setBackNum(orderItem.getBackNum());
                platformOrderItem.setSkuId(orderItem.getSkuId());
                platformOrderItem.setShipmentNum(orderItem.getShipmentNum());
                platformOrderItemList.add(platformOrderItem);
            }
        }
    }

    /**
     * 赠品
     * @param orderId
     * @param platformOrderItemList
     * @param orderItems
     */
    private void getGiftItems(long orderId, List<PlatformOrderItem> platformOrderItemList, List<OrderItem> orderItems) {
        String sql = " select * from t_order_gift where order_id = ? ";
        List<OrderGift> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper(OrderGift.class), orderId);
        if(result != null && result.size() > 0){
           for(OrderGift gift : result){
               StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnitWithStock(gift.getSkuId(), orderItems.get(0).getStorageId());
               PlatformOrderItem platformOrderItem=new PlatformOrderItem();
               Product product = productService.getProductById(gift.getProductId());
               ProductCategory productCategory = productCategoryService.getProductCategoryById(product.getCategoryId());
               Brand brand = supplierService.queryBrandById(product.getBrandId());
               String brandName = "";
               if (brand != null) {
                   brandName = brand.getName();
               }
               platformOrderItem.setOrderId(orderId);
               platformOrderItem.setBrandName(brandName);
               if (productCategory != null) {
                   platformOrderItem.setCategoryName(productCategory.getName());//类目名称，暂时没有
               }
               Money money = new Money();
               if (stockKeepingUnit != null) {
                   SkuStorage skustorage=skuStorageService.getSkuStorage(gift.getSkuId());
                   if(skustorage!=null){
                       platformOrderItem.setStockQuantity(skustorage.getStockQuantity());
                   }
                   money.setCent(0);
                   platformOrderItem.setSkuAttribute(skuService.getSkuPropertyToString(stockKeepingUnit));
                   platformOrderItem.setSkuPrice(money.toString());
                   money.setCent(0);
                   platformOrderItem.setUnitPrice(money.toString());
               } else {
                   platformOrderItem.setSkuAttribute("无");
                   platformOrderItem.setSkuPrice("0");
               }
               platformOrderItem.setId(0 - gift.getId());
               platformOrderItem.setUnitPrice("0");
               platformOrderItem.setProductName(gift.getProductName());
               platformOrderItem.setItemNo(stockKeepingUnit.getSkuCode());
               platformOrderItem.setBarCode(stockKeepingUnit.getBarCode());
               platformOrderItem.setShipmentNum(gift.getNumber());
               platformOrderItem.setSkuId(gift.getSkuId());
               platformOrderItemList.add(platformOrderItem);
           }
        }
    }


    private PlatformOrder generatePlatOrder(Order order) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PlatformOrder platformOrder=new PlatformOrder();
        //配送方式 的拼音，方便前端往后台传值
        platformOrder.setDeliveryTypePY(order.getDeliveryType().toString());
        platformOrder.setDeliveryType(order.getDeliveryType().toDesc());
        platformOrder.setStorageName(supplierService.queryProductStorageById(order.getStorageId()).getName());
        List<OrderItem> orderItems = tradeCenterSupplierClient.queryOrderItemWithoutBackingNumberByOrderId(order.getId());
        long old=0;
        for(OrderItem orderItem : orderItems){
            old += orderItem.totalPrice();
        }
        Money money = new Money();
        money.setCent(old);
        platformOrder.setProductTotalPrice(money.toString());
        platformOrder.setId(order.getId());
        platformOrder.setOrderNo(order.getOrderNo());
        platformOrder.setTotalPrice(order.getTotalPrice());
        platformOrder.setUserName(order.getUserName());
        platformOrder.setPayBank(order.getPayBank().toDesc());
        platformOrder.setAccountType(order.getAccountType().toDesc());
        String endDate = "";
        if (order.getEndDate() != null) {
            endDate = format.format(order.getEndDate());
        }
        platformOrder.setEndDate(endDate);
        String startDate = "";
        if (order.getCreateDate() != null) {
            startDate = format.format(order.getCreateDate());
        }
        if(order.getPayDate()==null){
            OrderStateHistory payHistory = tradeCenterSupplierClient.queryHistoryByState(order.getId(), OrderState.Pay);
            platformOrder.setPayDate(payHistory != null ? format.format(payHistory.getDate()) : "");
        }else{
            platformOrder.setPayDate(format.format(order.getPayDate()));
        }
        platformOrder.setStartDate(startDate);

        platformOrder.setOrderState(order.getOrderState().supplierDesc());

        InvoiceInfo invoiceInfo = tradeCenterSupplierClient.queryOrderInvoiceInfoRedundancy(order.getId());
        platformOrder.setInvoiceContent(invoiceInfo.getInvoiceContent());
        platformOrder.setInvoiceTitle(invoiceInfo.getInvoiceTitle().toDesc());
        platformOrder.setInvoice(invoiceInfo.isInvoice());
        platformOrder.setInvoiceType(invoiceInfo.getInvoiceType());
        platformOrder.setPriceMessageDetail(order.getPriceMessageDetail());
        platformOrder.setCompanyName(invoiceInfo.getCompanyName());
        platformOrder=generatePlatOrderRemark(platformOrder);
        platformOrder=generatePlatOrderStateTime(platformOrder);
        platformOrder=fetchModifyOrderInfo(platformOrder);
        return platformOrder;
    }


    /**
     * 获取一个订单的备注
     *
     * @param platformOrder
     * @return
     */
    private PlatformOrder generatePlatOrderRemark(PlatformOrder platformOrder) {
        OrderMessage userRemark = tradeCenterSupplierClient.queryUserMessage(platformOrder.getId());
        OrderMessage customerServiceRemark = tradeCenterSupplierClient.queryServerMessage(platformOrder.getId());
        if (customerServiceRemark != null) {
            platformOrder.setCustomerServiceRemark(customerServiceRemark.getMessageInfo());
        }
        if (userRemark != null) {
            platformOrder.setUserRemark(userRemark.getMessageInfo());
        }
        return platformOrder;
    }

    /**
     * 获取一个订单在不同状态时的时间
     *
     * @param platformOrder
     * @return
     */
    protected PlatformOrder generatePlatOrderStateTime(PlatformOrder platformOrder) {
        OrderStateHistory orderCreateHistory = tradeCenterSupplierClient.queryHistoryByState(platformOrder.getId(), OrderState.Create);
        OrderStateHistory orderPayHistory = tradeCenterSupplierClient.queryHistoryByState(platformOrder.getId(), OrderState.Pay);
        OrderStateHistory orderConfirmHistory = tradeCenterSupplierClient.queryHistoryByState(platformOrder.getId(), OrderState.Confirm);
        OrderStateHistory orderPrintHistory = tradeCenterSupplierClient.queryHistoryByState(platformOrder.getId(), OrderState.Print);
        OrderStateHistory orderVerifyHistory = tradeCenterSupplierClient.queryHistoryByState(platformOrder.getId(), OrderState.Verify);
        OrderStateHistory orderSendHistory = tradeCenterSupplierClient.queryHistoryByState(platformOrder.getId(), OrderState.Send);
        OrderStateHistory orderSuccessHistory = tradeCenterSupplierClient.queryHistoryByState(platformOrder.getId(), OrderState.Success);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (orderCreateHistory != null)
            platformOrder.setCreateTime(format.format(orderCreateHistory.getDate()));
        if (orderPayHistory != null)
            platformOrder.setPayTime(format.format(orderPayHistory.getDate()));
        if (orderConfirmHistory != null) {
            platformOrder.setConfirmTime(format.format(orderConfirmHistory.getDate()));
            platformOrder.setConfirmOperator(orderConfirmHistory.getOperator());
        }
        if (orderPrintHistory != null) {
            platformOrder.setPrintTime(format.format(orderPrintHistory.getDate()));
            platformOrder.setPrintOperator(orderPrintHistory.getOperator());
        }
        if (orderVerifyHistory != null) {
            platformOrder.setVerifyTime(format.format(orderVerifyHistory.getDate()));
            platformOrder.setVerifyOperator(orderVerifyHistory.getOperator());
        }
        if (orderSendHistory != null) {
            platformOrder.setSendTime(format.format(orderSendHistory.getDate()));
            platformOrder.setSendOperator(orderSendHistory.getOperator());
        }
        if (orderSuccessHistory != null) {
            platformOrder.setSuccessTime(format.format(orderSuccessHistory.getDate()));
        }
        return platformOrder;
    }


    /**
     * 查看订单物流信息是否修改
     *
     * @param platformOrder
     * @return
     */

    private PlatformOrder fetchModifyOrderInfo(PlatformOrder platformOrder) {
        Logistics logistics = tradeCenterSupplierClient.getLogisticsByOrderId(platformOrder.getId());
        LogisticsRedundancy logisticsRedundancy = tradeCenterSupplierClient.queryLogisticsRedundancy(logistics.getId());
        if (logisticsRedundancy.getZipCodeRewrite() != null){
            platformOrder.setZipCode(logisticsRedundancy.getZipCodeRewrite());
        }else{
            platformOrder.setZipCode(logistics.getZipCode());
        }

        if (logisticsRedundancy.getProvinceRewrite() != null){
            platformOrder.setProvince(logisticsRedundancy.getProvinceRewrite());
        }else{
            platformOrder.setProvince(logistics.getProvince());
        }

        if (logisticsRedundancy.getLocationRewrite() != null){
            platformOrder.setLocation(logisticsRedundancy.getLocationRewrite());
        }else{
            platformOrder.setLocation(logistics.getLocation());
        }

        if (logisticsRedundancy.getNameRewrite() != null){
            platformOrder.setName(logisticsRedundancy.getNameRewrite());
        }else{
            platformOrder.setName(logistics.getName());
        }

        if (logisticsRedundancy.getMobileRewrite() != null){
            platformOrder.setMobile(logisticsRedundancy.getMobileRewrite());
        }else{
            platformOrder.setMobile(logistics.getMobile());
        }
        platformOrder.setWaybillNumber(logistics.getDeliveryInfo().getWaybillNumber());
        String email="";
        if (logistics.getEmail() != null) {
            email = logistics.getEmail();
        }
        platformOrder.setEmail(email);
        platformOrder.setCost(logistics.getDeliveryInfo().getCost());

        return platformOrder;
    }
}
