package com.kariqu.suppliersystem.orderManager.vo;

import java.util.List;

/**
 * 抽象订单
 * User: wendy
 * Date: 13-8-27
 * Time: 下午5:34
 * To change this template use File | Settings | File Templates.
 */
public class PlatformOrder {

    private long id;

    /** 订单编号*/
    private long orderNo;

    /** 物流单号 */
    private String waybillNumber;

    /** 收获人姓名 */
    private String name;

    /** 买家姓名 */
    private String userName;

    /** 订单状态 */
    private String orderState;

    /** 配送方式 */
    private String deliveryType;

    /**配送方式，英文，方便前台在打印时传参*/
    private String deliveryTypePY;

    /** 账户类型，可区别出来自什么网站，比如QQ，sina，KRQ代表我们自己 */
    private String accountType;

    /** 库房名称 */
    private String storageName;

    /** 应付的订单总金额 ，除去优惠劵和积分*/
    private String totalPrice;

    /** 商品总金额*/
    private String productTotalPrice;

    /**付款总金额*/
    private String payTotalPrice;

    /** 下单时间 */
    private String startDate;

    /** 结束时间 */
    private String endDate;

    /** 付款时间 */
    private String payDate;

    /** 卖家留言 */
    private String customerServiceRemark;

    /** 买家留言 */
    private String userRemark;

    /** 省份，比如浙江*/
    private String province;

    /** 具体位置，到门牌号*/
    private String location;

    /** 支付方式*/
    private String payBank;

    /** 联系电话*/
    private String mobile;

    /** 运费*/
    private String cost;

    /** 邮编*/
    private String zipCode;

    /** 邮件地址*/
    private String email;

    /** 是否开发票*/
    private boolean invoice;

    /** 发票类型，比如普通发票，增值税*/
    private String invoiceType;

    /** 抬头，个人或者单位*/
    private String invoiceTitle;

    /** 单位名字*/
    private String companyName;

    /** 发票内容，明细，办公用品等*/
    private String invoiceContent;

    /** 订单创建日期*/
    private String createTime;

    /** 订单付款日期*/
    private String payTime;

    /** 审核日期*/
    private String confirmTime;
    private String confirmOperator;

    /** 打印日期*/
    private String printTime;
    private String printOperator;

    /** 验货日期*/
    private String verifyTime;
    private String verifyOperator;

    /** 发货日期*/
    private String sendTime;
    private String sendOperator;

    /** 使用现金券或积分时记录其订单价格相关详细说明 */
    private String priceMessageDetail;

    /** 签收时间*/
    private String successTime;

    /** 所属平台*/
    private String platform;

    private List<PlatformOrderItem> platformOrderItemList;

    public String getDeliveryTypePY() {
        return deliveryTypePY;
    }

    public void setDeliveryTypePY(String deliveryTypePY) {
        this.deliveryTypePY = deliveryTypePY;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }



    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(String productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getPayTotalPrice() {
        return payTotalPrice;
    }

    public void setPayTotalPrice(String payTotalPrice) {
        this.payTotalPrice = payTotalPrice;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getCustomerServiceRemark() {
        return customerServiceRemark;
    }

    public void setCustomerServiceRemark(String customerServiceRemark) {
        this.customerServiceRemark = customerServiceRemark;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPayBank() {
        return payBank;
    }

    public void setPayBank(String payBank) {
        this.payBank = payBank;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean isInvoice() {
        return invoice;
    }

    public void setInvoice(boolean invoice) {
        this.invoice = invoice;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getConfirmOperator() {
        return confirmOperator;
    }

    public void setConfirmOperator(String confirmOperator) {
        this.confirmOperator = confirmOperator;
    }

    public String getPrintTime() {
        return printTime;
    }

    public void setPrintTime(String printTime) {
        this.printTime = printTime;
    }

    public String getPrintOperator() {
        return printOperator;
    }

    public void setPrintOperator(String printOperator) {
        this.printOperator = printOperator;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getVerifyOperator() {
        return verifyOperator;
    }

    public void setVerifyOperator(String verifyOperator) {
        this.verifyOperator = verifyOperator;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendOperator() {
        return sendOperator;
    }

    public void setSendOperator(String sendOperator) {
        this.sendOperator = sendOperator;
    }

    public String getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(String successTime) {
        this.successTime = successTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PlatformOrderItem> getPlatformOrderItemList() {
        return platformOrderItemList;
    }

    public void setPlatformOrderItemList(List<PlatformOrderItem> platformOrderItemList) {
        this.platformOrderItemList = platformOrderItemList;
    }

    public String getPriceMessageDetail() {
        return priceMessageDetail;
    }

    public void setPriceMessageDetail(String priceMessageDetail) {
        this.priceMessageDetail = priceMessageDetail;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
