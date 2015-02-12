package com.kariqu.tradesystem.web;

/**
 * @author Athens(刘杰)
 * @Time 13-9-17 上午10:51
 */
public class ExportOrder {

    /** 支付方式 */
    private String payBank;

    /** 下单时间 */
    private String createDate;

    /** 完成时间 */
    private String successDate;

    /** 下单人 */
    private String userName;

    /** 订单编号 */
    private String orderNo;

    /** 品牌 */
    private String brandName;

    /** 商品名 */
    private String productName;

    /** 商品编码 */
    private String productCode;

    /** 购买数量 */
    private String number;

    /** 购买单价 */
    private String unitPrice;

    /** 应付总金额 */
    private String oldTotalPrice;

    /** 积分/优惠拳说明 */
    private String priceMessageDetail;

    /** 实际付款金额 */
    private String totalPrice;

    /** 退货数量 */
    private String backNumber;

    /** 订单状态 */
    private String orderState;

    /** 备注(发票及留言) */
    private String remark;

    /** 收货人 */
    private String logisticsName;

    /** 收货人地址 */
    private String logisticsAddress;

    /** 收货人联系电话 */
    private String logisticsPhone;

    /** 订单项总数(主要用来合并单元格) */
    private int itemSize;

    public String getPayBank() {
        return payBank;
    }

    public void setPayBank(String payBank) {
        this.payBank = payBank;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getSuccessDate() {
        return successDate;
    }

    public void setSuccessDate(String successDate) {
        this.successDate = successDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getOldTotalPrice() {
        return oldTotalPrice;
    }

    public void setOldTotalPrice(String oldTotalPrice) {
        this.oldTotalPrice = oldTotalPrice;
    }

    public String getPriceMessageDetail() {
        return priceMessageDetail;
    }

    public void setPriceMessageDetail(String priceMessageDetail) {
        this.priceMessageDetail = priceMessageDetail;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBackNumber() {
        return backNumber;
    }

    public void setBackNumber(String backNumber) {
        this.backNumber = backNumber;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getLogisticsAddress() {
        return logisticsAddress;
    }

    public void setLogisticsAddress(String logisticsAddress) {
        this.logisticsAddress = logisticsAddress;
    }

    public String getLogisticsPhone() {
        return logisticsPhone;
    }

    public void setLogisticsPhone(String logisticsPhone) {
        this.logisticsPhone = logisticsPhone;
    }

    public int getItemSize() {
        return itemSize;
    }

    public void setItemSize(int itemSize) {
        this.itemSize = itemSize;
    }
}
