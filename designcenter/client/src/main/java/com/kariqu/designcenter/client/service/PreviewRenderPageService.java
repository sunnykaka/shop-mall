package com.kariqu.designcenter.client.service;

import com.kariqu.designcenter.client.domain.model.RenderResult;

import java.util.Map;

/**预览环境下渲染页面
 * @author Tiger
 * @since 2011-4-12 下午10:42:26 
 * @version 1.0.0
 */
public interface PreviewRenderPageService {

    public RenderResult previewRenderPage(long pageId, Map<String,Object> context);
}
