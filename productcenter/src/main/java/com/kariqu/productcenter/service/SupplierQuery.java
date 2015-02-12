package com.kariqu.productcenter.service;

/**
 * User: Tony
 * Date: 12-12-14
 */
public class SupplierQuery {

    /** 库房编号 */
    private Integer storeId;

    /** 条形码 */
    private String barCode;

    /** 商品编码 */
    private String productCode;

    /** 商品名模糊查询 */
    private String productName;

    /** 日期类型. [sku 创建日期、修改日期] */
    private String dateType;

    /** 开始时间. YYYY-MM-DD 格式 */
    private String startDate;

    /** 结束时间. YYYY-MM-DD 格式 */
    private String endDate;

    /** 分页查询 */
    private Integer start;

    /** 分页 */
    private int limit;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    /** create 或 update */
    public String getDateType() {
        return dateType;
    }

    /** create 或 update */
    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
