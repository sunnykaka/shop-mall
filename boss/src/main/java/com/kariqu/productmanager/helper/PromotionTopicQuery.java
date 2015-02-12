package com.kariqu.productmanager.helper;

/**
 * User: Baron.Zhang
 * Date: 2014/10/17
 * Time: 14:07
 */
public class PromotionTopicQuery {

    private String name;

    private String topic;

    private int start;

    private int limit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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
