package com.kariqu.designcenter.domain.model.prototype;

/**
 * 模块粒度
 *
 * @author Tiger
 * @version 1.0
 * @since 12-12-20 下午1:22
 */
public enum ModuleGranularity {

    /**
     * 全局粒度类型，这种模块无论添加到任何地方数据都是一样的
     */
    GLOBAL,

    /**
     * 页面粒度类型，这种模块在整个页面无论添加多少次都是唯一的
     */
    //PAGE,

    /**
     * 缺省的粒度类型，这种模块每一个参数都不一样
     */
    DEFAULT


}
