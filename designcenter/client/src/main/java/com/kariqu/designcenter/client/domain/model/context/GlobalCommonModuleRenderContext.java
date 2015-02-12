package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceIdFactory;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * 全局模块渲染上下文，主要用于非装修页面渲染全局模块
 *
 * @author Tiger
 * @version 1.0
 * @since 12-12-21 下午2:26
 */
public class GlobalCommonModuleRenderContext extends EditRenderModuleContext {


    private ShopTemplate shopTemplate;

    public GlobalCommonModuleRenderContext(CommonModule commonModule, RenderEngine renderEngine, ShopTemplate shopTemplate) {
        super(commonModule, renderEngine);
        this.shopTemplate = shopTemplate;
    }

    @Override
    protected String doRender() {
        String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfGlobalGranularity(1, commonModule.getId());
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getGlobalModuleInfoConfig());
        if (pageStructure == null) {
            logger.error("全局公共模块配置信息为空，shopTemplateId=" + shopTemplate.getId());
            return StringUtils.EMPTY;
        }
        Module module = pageStructure.getModuleById(moduleInstanceId);
        module.setModulePrototype(commonModule);
        Map<String, Object> moduleContext = moduleContextExecutor.execute(getAllContext(), module);
        if (moduleContext != null) {
            putAll(moduleContext);
        }
        return renderEngine.render(module, getAllContext());
    }
}
