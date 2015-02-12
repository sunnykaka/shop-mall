package com.kariqu.designcenter.client.service;

import com.kariqu.designcenter.client.domain.model.RenderResult;

import java.util.Map;

/**
 * 渲染店铺的头尾
 * 在非原型页面中使用
 * @author Tiger
 * @version 1.0
 * @since 12-5-9 上午11:23
 */
public interface HeadFootRenderService {

    /**
     * 渲染店铺头部
     *
     * @param shopId
     * @param context
     * @return
     */
    RenderResult renderHead(int shopId, Map<String, Object> context);

    /**
     * 渲染店铺页尾
     *
     * @param shopId
     * @param context
     * @return
     */
    RenderResult renderFoot(int shopId, Map<String, Object> context);
}
