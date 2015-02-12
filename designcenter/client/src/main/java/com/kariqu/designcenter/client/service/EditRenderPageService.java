package com.kariqu.designcenter.client.service;

import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.RenderConstants;

import java.util.Map;

/**
 * 用于编辑模式下渲染页面
 *
 * @author Tiger
 * @version 1.0
 * @since 2011-4-4 下午07:54:50
 */
public interface EditRenderPageService {

    /**
     * 渲染页面
     *
     * @param pageId
     * @param context
     * @return
     */
    public RenderResult editRenderPage(long pageId, Map<String, Object> context);

    /**
     * 渲染公共模块的编辑表单
     *
     * @param context
     * @param module
     * @return
     */
    public String renderCommonModuleEditForm(Map<String, Object> context, Module module);

    /**
     * 渲染模块，用于装修页面的时候，添加模块成功以后的渲染
     *
     * @param moduleInstanceId
     * @param prototypeId
     * @param context
     * @param renderArea
     * @return
     */
    public String renderCommonModuleWithToolbar(String moduleInstanceId, int prototypeId, Map<String, Object> context, RenderConstants.RenderArea renderArea);
}
