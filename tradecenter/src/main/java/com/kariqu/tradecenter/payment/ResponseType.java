package com.kariqu.tradecenter.payment;

/**
 * 调用回调处理类的方式
 * User: amos
 * Date: 13-11-4
 * Time: 下午2:45
 *
 */
public enum  ResponseType {
    /**
     * 正常返回
     */
    RETURN("return"),
    /**
     * Notify回来
     */
    NOTIFY("notify");

    private String value;

    ResponseType(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
