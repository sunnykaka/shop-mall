package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderContext;
import com.kariqu.designcenter.domain.exception.RenderException;

import java.util.List;
import java.util.Map;

/**
 * 空对象模式应用，防止外层客户端调用产生npe
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-21 下午09:12:15
 */
public class NullRenderContext implements RenderContext {


    private String message;

    public NullRenderContext(String message) {
        this.message = message;

    }

    @Override
    public String render() {
        throw new RenderException(message);
    }

    @Override
    public Map<String, String> getResultParams() {
        return null;
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        return null;
    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> moduleInfoList) {
        return null;
    }

    @Override
    public String renderTemplateModule(String name, int domId) {
        return null;
    }

}
