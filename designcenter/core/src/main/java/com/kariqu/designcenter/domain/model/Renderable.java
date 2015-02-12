package com.kariqu.designcenter.domain.model;

import java.util.Map;

/**
 * 渲染能力接口，如果实体是可渲染的，它必须提供内容和参数
 * 
 * @author Tiger
 * @since 2010-12-16 下午05:30:51
 * @version 1.0
 */
public interface Renderable {

    /**
     * 获取待渲染内容
     * 
     * @return
     */
    public String getContent();

    /**
     * 获取渲染上下文
     * 
     * @return
     */
    public Map<String, Object> getContext();
}
