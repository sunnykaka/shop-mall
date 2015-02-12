package com.kariqu.tradecenter.domain;

import java.util.List;

/**
 * 与退货单相关的所有返回数据.(也会返回订单数据)
 *
 * @author Athens(刘杰)
 * @Time 2012-12-27 09:35
 * @since 1.0.0
 */
public class Result<T, D> {

    /**
     * 返回结果, 成功(true) 或 失败(false)
     */
    private boolean success = false;

    /**
     * 返回的数据集
     */
    private List<T> list;

    /**
     * 返回的可能只有一个对象
     */
    private D obj;

    /**
     * 若失败, 错误信息存于此
     */
    private String message;

    public Result() {
    }

    public Result(boolean success, List<T> list, D obj, String message) {
        this.success = success;
        this.list = list;
        this.obj = obj;
        this.message = message;
    }

    /**
     * 返回结果, 成功(true) 或 失败(false)
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    /**
     * 返回的可能只有一个对象
     */
    public List<T> getList() {
        return list;
    }

    /**
     * 返回的可能只有一个对象
     */
    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * 返回的可能是一个对象
     */
    public D getObj() {
        return obj;
    }

    /**
     * 返回的可能是一个对象
     */
    public void setObj(D obj) {
        this.obj = obj;
    }

    /**
     * 若失败, 错误信息存于此
     */
    public String getMessage() {
        return message;
    }

    /**
     * 若失败, 错误信息存于此
     */
    public void setMessage(String message) {
        this.message = message;
    }


}
