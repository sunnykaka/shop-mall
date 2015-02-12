package com.kariqu.productcenter.domain;

/**
 * 商品SKU修改异常
 * 必须捕获这个异常进行处理
 * User: Asion
 * Date: 12-6-6
 * Time: 上午11:35
 */
public class SkuStockModifyException extends RuntimeException {

    public SkuStockModifyException() {
        super();
    }

    public SkuStockModifyException(String message) {
        super(message);
    }

    public SkuStockModifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkuStockModifyException(Throwable cause) {
        super(cause);
    }
}
