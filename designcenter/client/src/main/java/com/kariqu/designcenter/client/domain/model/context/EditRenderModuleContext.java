package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.domain.model.AbstractRenderContext;
import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.util.RenderUtil;
import com.kariqu.designcenter.service.ModuleContextExecutor;
import com.kariqu.designcenter.service.ModuleInstanceParamService;

import java.util.List;
import java.util.Map;

/**
 * 装修的时候，添加模块成功以后渲染模块
 *
 * @author Tiger
 * @version 1.0
 * @since 12-5-16 下午3:24
 */
public class EditRenderModuleContext extends AbstractRenderContext {


    private RenderConstants.RenderArea renderArea;

    protected CommonModule commonModule;

    private URLBrokerFactory urlBrokerFactory;

    private String moduleInstanceId;

    protected ModuleContextExecutor moduleContextExecutor;

    protected ModuleInstanceParamService moduleInstanceParamService;


    public EditRenderModuleContext(String moduleInstanceId, CommonModule commonModule, RenderEngine renderEngine, RenderConstants.RenderArea renderArea) {
        super(renderEngine);
        this.renderArea = renderArea;
        this.moduleInstanceId = moduleInstanceId;
        this.commonModule = commonModule;
    }

    public EditRenderModuleContext(CommonModule commonModule, RenderEngine renderEngine) {
        super(renderEngine);
        this.commonModule = commonModule;
    }


    @Override
    protected String doRender() {
        Module module = new Module(moduleInstanceId, commonModule);
        switch (renderArea) {
            case head:
                module.setToolbar(RenderUtil.initHeadToolbar(module, urlBrokerFactory));
                break;
            case foot:
                module.setToolbar(RenderUtil.initFootToolbar(module, urlBrokerFactory));
                break;
            case body:
                module.setToolbar(RenderUtil.initBodyToolbar(module, urlBrokerFactory));
                break;
        }

        put(RenderConstants.TOOL_BAR, module.getToolbar().toString());
        List<ModuleInstanceParam> params = moduleInstanceParamService.queryModuleParamsByModuleInstanceId(moduleInstanceId);
        module.setParams(params);
        Map<String, Object> moduleContext = moduleContextExecutor.execute(getAllContext(), module);
        if (moduleContext != null) {
            putAll(moduleContext);
        }
        return renderEngine.render(module, getAllContext());
    }

    @Override
    public String renderTemplateModule(String name, int domId) {
        throw new UnsupportedOperationException("模块中不能嵌套模块");
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        throw new UnsupportedOperationException("模块代码中不能嵌套模块");
    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> moduleInfoList) {
        throw new UnsupportedOperationException("模块代码中不能嵌套坑");
    }

    public void setUrlBrokerFactory(URLBrokerFactory urlBrokerFactory) {
        this.urlBrokerFactory = urlBrokerFactory;
    }

    public void setModuleContextExecutor(ModuleContextExecutor moduleContextExecutor) {
        this.moduleContextExecutor = moduleContextExecutor;
    }

    public void setModuleInstanceParamService(ModuleInstanceParamService moduleInstanceParamService) {
        this.moduleInstanceParamService = moduleInstanceParamService;
    }
}

