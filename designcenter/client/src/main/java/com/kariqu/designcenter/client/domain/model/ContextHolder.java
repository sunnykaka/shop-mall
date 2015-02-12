package com.kariqu.designcenter.client.domain.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 渲染下文管理器
 * 各种渲染上下文继承这个类获得上下文管理能力
 *
 * @author Asion
 * @version 1.0.0
 * @since 2011-5-5 下午02:45:48
 */
abstract class ContextHolder {

    /**
     * 保护构造器
     */
    protected ContextHolder() {

    }

    /**
     * 全局上下文
     */
    protected Map<String, Object> globalContext = new HashMap<String, Object>();


    /**
     * 开放接口环境变量
     */
    protected Map<String, Object> openApiContext = new HashMap<String, Object>();


    /**
     * 返回结果参数
     */
    protected Map<String, String> resultParams = new HashMap<String, String>();

    /**
     * 获取所有渲染上下文
     * 这个上下文中包含所有的渲染数据
     *
     * @return
     */
    public Map<String, Object> getAllContext() {
        return globalContext;
    }


    public Object get(String key) {
        return globalContext.get(key);
    }

    public void put(String key, Object value) {
        this.globalContext.put(key, value);
    }

    public void putAll(Map<String, Object> context) {
        this.globalContext.putAll(context);
    }

    public void putGlobalParameter(Map<String, Object> context) {
        if (context != null && !context.isEmpty())
            this.globalContext.putAll(context);
    }


    public void setOpenApiContext(Map<String, Object> openApiContext) {
        if (openApiContext != null && !openApiContext.isEmpty()) {
            this.openApiContext = openApiContext;
            this.globalContext.putAll(openApiContext);
        }
    }


    public Map<String, String> getResultParams() {
        return resultParams;
    }


    public void putResultParam(String key, String value) {
        this.resultParams.put(key, value);
    }


}
