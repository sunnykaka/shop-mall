package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.exception.DomIdRepeatException;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.PagePrototype;
import com.kariqu.designcenter.service.CommonModuleService;
import com.kariqu.designcenter.service.ModuleContextExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 渲染页面原型上下文
 *
 * @author Tiger
 * @version 1.0
 * @since 12-4-19 下午3:51
 */
public class DebugPagePrototypeRenderContext extends DomCheckRenderContext {

    private PagePrototype pagePrototype;

    private CommonModuleService commonModuleService;

    private ModuleContextExecutor moduleContextExecutor;

    private StringBuilder jsBuffer = new StringBuilder("<script type=\"text/javascript\">");

    /**
     * 渲染模块js的标志map，用于标记哪个模块的js是否被解析过
     */
    private Map<Integer, Boolean> jsRenderMap = new HashMap<Integer, Boolean>();


    public DebugPagePrototypeRenderContext(PagePrototype pagePrototype, RenderEngine renderEngine) {
        super(renderEngine);
        this.pagePrototype = pagePrototype;
    }


    @Override
    public String doRender() {
        String content = renderEngine.render(pagePrototype.getPageCode(), getAllContext());
        if (!isValid()) {
            throw new DomIdRepeatException("页面DomID出现重复，请查看:" + getDomIdError());
        }
        jsBuffer.append("</script>");
        return content + jsBuffer.toString();
    }

    @Override
    public String renderTemplateModule(String name, int domId) {
        throw new UnsupportedOperationException("页面原型不支持模板模块");
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        CommonModule commonModule = commonModuleService.getCommonModuleForRendering(name, version);
        if (commonModule == null) {
            throw new RuntimeException("公共模块不存在，模块名称=" + name);
        }
        try {
            Map<String, Object> context = moduleContextExecutor.executeForDebug(getAllContext(), commonModule);
            if (context == null) {
                context = new HashMap<String, Object>();
                logger.warn("模块:" + name + "的脚本执行返回空map");
            }
            context.putAll(commonModule.getContext());
            context.putAll(getAllContext());
            String editModuleJs = commonModule.getEditModuleJs();
            if (StringUtils.isNotEmpty(editModuleJs)) {
                int id = commonModule.getId();
                if (jsRenderMap.get(id) == null) {
                    editModuleJs = editModuleJs.replace("<script type=\"text/javascript\">", "");
                    editModuleJs = editModuleJs.replace("</script>", "");
                    jsBuffer.append(editModuleJs);
                    jsRenderMap.put(id, true);
                }
            }
            return renderEngine.render(wrapperModule(commonModule.getEditModuleContent()), context);
        } catch (Exception e) {
            logger.error("公共模块渲染错误", e);
            return ExceptionUtils.getFullStackTrace(e);
        }
    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> modules) {
        StringBuilder regionContent = new StringBuilder();
        for (ModuleInfo moduleInfo : modules) {
            if (moduleInfo.isTemplateModule()) {
                regionContent.append(renderTemplateModule(moduleInfo.getName(), moduleInfo.getDomId()));
            } else {
                regionContent.append(renderCommonModule(moduleInfo.getName(), moduleInfo.getVersion(), moduleInfo
                        .getDomId()));
            }
        }
        return wrapperRegion(regionContent.toString());
    }

    public void setCommonModuleService(CommonModuleService commonModuleService) {
        this.commonModuleService = commonModuleService;
    }

    public void setModuleContextExecutor(ModuleContextExecutor moduleContextExecutor) {
        this.moduleContextExecutor = moduleContextExecutor;
    }

}
