package com.kariqu.common.lib;

/**
 * Created by Canal.wen on 2014/6/27 14:45.
 */
public interface Action<T> {
    void invoke(T result);
}
