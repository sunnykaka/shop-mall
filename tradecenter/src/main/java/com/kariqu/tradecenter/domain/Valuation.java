package com.kariqu.tradecenter.domain;

import com.kariqu.common.DateUtils;
import com.kariqu.usercenter.domain.UserGrade;
import org.apache.commons.lang.StringUtils;
import java.util.Date;

/**
 * 用户评价
 * User: Asion
 * Date: 13-3-4
 * Time: 下午4:56
 */
public class Valuation {

    private int id;

    private int userId;

    private String userName;

    private String content;

    private long orderItemId;

    private int productId;

    private Date orderCreateDate;

    private Date createDate;

    private Date updateDate;

    private int point;

    private int operatorId;

    private String operator;

    private String replyContent;

    private Date replyTime;

    private UserGrade grade;

    /**
     * 追加评价
     */
    private String appendContent;

    /**
     * 追加时间
     */
    private Date appendDate;


    /**
     * 追加回复评价
     */
    private String appendReplyContent;

    /**
     * 追加回复评价的操作者
     */
    private String appendOperator;

    /**
     * 追加回复时间
     */
    private Date appendReplyDate;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public UserGrade getGrade() {
        return grade;
    }

    public void setGrade(UserGrade grade) {
        this.grade = grade;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getCreateDateStr() {
        return  DateUtils.formatDate(this.getCreateDate(), DateUtils.DateFormatType.DATE_MINUTE_FORMAT_STR);
    }


    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public String getUpdateDateStr() {
        return  DateUtils.formatDate(this.getUpdateDate(), DateUtils.DateFormatType.DATE_MINUTE_FORMAT_STR);
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public Date getOrderCreateDate() {
        return orderCreateDate;
    }

    public String getOrderCreateDateStr() {
        return  DateUtils.formatDate(this.getOrderCreateDate(), DateUtils.DateFormatType.DATE_MINUTE_FORMAT_STR);
    }

    public void setOrderCreateDate(Date orderCreateDate) {
        this.orderCreateDate = orderCreateDate;
    }

    public String getUserName() {
        if (StringUtils.isNotBlank(userName) && userName.length() > 2) {
            return userName.substring(0, 1)
                    + userName.substring(1, userName.length() - 1).replaceAll(".", "*")
                    + userName.substring(userName.length() - 1);
        }
        return userName;
    }

    public String getUserAllName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public String getReplyTimeStr() {
        return  DateUtils.formatDate(this.getReplyTime(), DateUtils.DateFormatType.DATE_MINUTE_FORMAT_STR);
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    /**
     * 客服回复是否在用户追加之前, 若在之前则返回 true
     */
    public boolean replyBeforeAppend() {
        // 追加时间为空 或 回复时间在追加时间之前
        return (appendDate == null) || (replyTime != null && replyTime.before(appendDate));
    }

    /**
     * 是否有客服回复或用户追加. 若有则返回 true
     */
    public boolean replyOrAppend() {
        return StringUtils.isNotBlank(replyContent) || StringUtils.isNotBlank(appendContent);
    }

    public String getAppendContent() {
        return appendContent;
    }

    public void setAppendContent(String appendContent) {
        this.appendContent = appendContent;
    }

    public String getAppendReplyContent() {
        return appendReplyContent;
    }

    public void setAppendReplyContent(String appendReplyContent) {
        this.appendReplyContent = appendReplyContent;
    }

    public Date getAppendDate() {
        return appendDate;
    }

    public String getAppendDateStr() {
        return  DateUtils.formatDate(this.getAppendDate(), DateUtils.DateFormatType.DATE_MINUTE_FORMAT_STR);
    }

    public void setAppendDate(Date appendDate) {
        this.appendDate = appendDate;
    }

    public Date getAppendReplyDate() {
        return appendReplyDate;
    }

    public String getAppendReplyDateStr() {
        return  DateUtils.formatDate(this.getAppendReplyDate(), DateUtils.DateFormatType.DATE_MINUTE_FORMAT_STR);
    }

    public void setAppendReplyDate(Date appendReplyDate) {
        this.appendReplyDate = appendReplyDate;
    }

    public String getAppendOperator() {
        return appendOperator;
    }

    public void setAppendOperator(String appendOperator) {
        this.appendOperator = appendOperator;
    }

    @Override
    public String toString() {
        return "Valuation{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", orderItemId=" + orderItemId +
                ", productId=" + productId +
                ", orderCreateDate=" + orderCreateDate +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", point=" + point +
                ", operatorId=" + operatorId +
                ", operator='" + operator + '\'' +
                ", replyContent='" + replyContent + '\'' +
                ", replyTime=" + replyTime +
                ", appendContent='" + appendContent + '\'' +
                ", appendDate=" + appendDate +
                ", appendReplyContent='" + appendReplyContent + '\'' +
                ", appendReplyDate=" + appendReplyDate +
                '}';
    }
}
