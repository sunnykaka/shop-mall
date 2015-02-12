package com.kariqu.searchengine.domain;

/**
 * 统计泛型类
 * 一般按叶子类目ID和pidivd统计
 * @Author: Tiger
 * @Since: 11-6-26 下午3:18
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class CountStatsNode<T> {

    private T type;

    private long count;

    public CountStatsNode(T type, long count) {
        this.type = type;
        this.count = count;
    }

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "CountStatNode{" +
                "type=" + type +
                ", count=" + count +
                '}';
    }
}
