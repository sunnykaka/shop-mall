package com.kariqu.designcenter.client;

import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderContext;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.client.infrastructure.VelocityRenderEngine;
import com.kariqu.designcenter.domain.model.RenderConstants;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tiger
 * @version 1.0
 * @since 2010-12-19 下午04:48:58
 */
public class VelocityDirectiveTest {

    RenderEngine renderEngine = new VelocityRenderEngine();

    @Test
    public void testImportModule() {

        try {
            RenderContext renderContext = new RenderContext() {

                @Override
                public String render() {
                    return null;
                }

                @Override
                public Map<String, String> getResultParams() {
                    return null;
                }

                @Override
                public String renderCommonModule(String name, String version,
                                                 int domId) {
                    return "渲染公共模块: 模块名称=" + name + " 版本=" + version + " domId"
                            + domId;
                }

                @Override
                public String renderRegion(String regionName,
                                           List<ModuleInfo> modules) {
                    StringBuilder result = new StringBuilder();
                    result.append("渲染坑： 坑名称=")
                            .append(regionName)
                            .append(" 坑内模块列表");
                    for (ModuleInfo moduleInfo : modules) {
                        result.append(moduleInfo.toString());
                    }

                    return result.toString();
                }

                @Override
                public String renderTemplateModule(String name, int domId) {
                    return "渲染模板私有模块: 模块名称=" + name + " domId=" + domId;
                }

            };

            Map<String, Object> context = new HashMap<String, Object>();
            context.put(RenderConstants.RENDER_CONTEXT, renderContext);
            String content = renderEngine.render(IOUtils.toString(this.getClass()
                    .getClassLoader().getResourceAsStream("directive.vm")), context);
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
