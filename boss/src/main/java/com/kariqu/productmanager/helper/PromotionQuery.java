package com.kariqu.productmanager.helper;

/**
 * User: Baron.Zhang
 * Date: 2014/10/17
 * Time: 14:07
 */
public class PromotionQuery {

    private int topicId;

    private int start;

    private int limit;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
