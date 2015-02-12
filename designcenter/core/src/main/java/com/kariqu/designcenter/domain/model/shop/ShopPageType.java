package com.kariqu.designcenter.domain.model.shop;

import java.io.Serializable;

/**
 * 店铺页面类型
 *
 * @author Tiger
 * @version 1.0
 * @since 2011-1-17 上午11:48:46
 */
public enum ShopPageType implements Serializable {

    /**
     * 模板页面，表示是某个模板相关联的页面
     */
    TEMPLATE_PAGE,

    /**
     * 原型页面，通过页面原型生成的店铺页面
     */
    PROTOTYPE_PAGE,

    /**
     * 系统页面，是平台提供的页面
     */
    SYSTEM_PAGE,

    /**
     * 是店铺自定义的页面，此种类型的页面与模板无关
     */
    CUSTOM_PAGE

}
