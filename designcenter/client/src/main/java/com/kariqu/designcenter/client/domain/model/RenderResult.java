package com.kariqu.designcenter.client.domain.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 页面渲染结果
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-21 下午09:35:20
 */
public class RenderResult implements Serializable {

    /* 页面内容*/
    private String pageContent;

    /* 页面的 css js资源 */
    private Map<String, String> resourceParams;


    /**
     * @param pageContent
     * @param resourceParams
     */
    public RenderResult(String pageContent, Map<String, String> resourceParams) {
        this.pageContent = pageContent;
        this.resourceParams = resourceParams;
    }


    public String getResourceParam(String key) {
        return resourceParams.get(key);
    }


    /**
     * @return the pageContent
     */
    public String getPageContent() {
        return pageContent;
    }


}
