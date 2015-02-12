package com.kariqu.productcenter.domain;

import com.kariqu.common.DateUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * 积分活动基类
 * Created by Canal.wen on 2014/8/5 13:41.
 */
public class ProductConversionBase {
    private int id;
    /**商品id */
    private int productId;
    /**商品sku id*/
    private int skuId;

    /**积分兑换开始时间 */
    private Date startDate;
    /**积分兑换结束时间 */
    private Date endDate;

    /**创建时间 */
    private Date createDate;
    /** 结束时间*/
    private Date updateDate;

    private int mockSale;

    private int realSale;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStart(String start) {
        startDate = DateUtils.parseDate(start, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
    }

    public String getStart() {
        return DateUtils.formatDate(startDate, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
    }

    /**
     * 按 dateFormat 的格式, 格式化 startDate. 如果为　null 返回 "" 空字符串
     * @param dateFormat
     * @return
     */
    public String fetchStartDateWithFormat(String dateFormat) {
        if (startDate != null) {
            return DateFormatUtils.format(startDate, dateFormat);
        } else {
            return "";
        }
    }

    public void setEnd(String end) {
        endDate = DateUtils.parseDate(end, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
    }

    public String getEnd() {
        return DateUtils.formatDate(endDate, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * 按 dateFormat 的格式, 格式化 endDate. 如果为　null 返回 "" 空字符串
     * @param dateFormat
     * @return
     */
    public String fetchEndDateWithFormat(String dateFormat) {
        if (endDate != null) {
            return DateFormatUtils.format(endDate, dateFormat);
        } else {
            return "";
        }
    }

    public int getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public int getMockSale() {
        return mockSale;
    }

    public void setMockSale(int mockSale) {
        this.mockSale = mockSale;
    }

    public int getRealSale() {
        return realSale;
    }

    public void setRealSale(int realSale) {
        this.realSale = realSale;
    }

    public int fetchSalCount() {
        return realSale + mockSale;
    }

}
