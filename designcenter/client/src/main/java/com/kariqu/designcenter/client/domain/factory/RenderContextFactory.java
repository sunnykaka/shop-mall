package com.kariqu.designcenter.client.domain.factory;

import com.kariqu.designcenter.client.domain.model.RenderContext;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.PagePrototype;
import com.kariqu.designcenter.domain.model.prototype.TemplatePage;
import com.kariqu.designcenter.domain.model.prototype.TemplateVersion;

import java.util.Map;

/**
 * 渲染上下文工厂
 * 一个页面可能处于各种渲染场景：
 * 1，调试
 * 2，装修
 * 3，预览
 * 4，产品
 * 5，结构提取
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-21 下午03:12:59
 */
public interface RenderContextFactory {

    /**
     * 创建生产环境的页面渲染上下文
     *
     * @param pageId
     * @param context
     * @return
     */
    RenderContext createProdRenderContext(long pageId, Map<String, Object> context);

    /**
     * 创建页面编辑模式的渲染上下文，即在装修页面的时候对页面的渲染
     *
     * @param pageId
     * @return
     */
    RenderContext createEditRenderContext(long pageId, Map<String, Object> context);

    /**
     * 创建预览模式的渲染上下文，即编辑完页面以后进行预渲染时对应的上下文
     *
     * @param pageId
     * @return
     */
    RenderContext createPreviewRenderContext(long pageId, Map<String, Object> context);

    /**
     * 创建在线制作模板的过程中页面渲染的逻辑
     *
     * @param pageId
     * @return
     */
    RenderContext createDebugTemplatePageRenderContext(int pageId, Map<String, Object> context);

    /**
     * 创建初始化渲染上下文，用于发布模板的时候生成页面配置文件
     *
     * @param templatePage
     * @return
     */
    RenderContext createInitPageRenderContext(TemplatePage templatePage);

    /**
     * 创建初始化模板头部的渲染上下文
     *
     * @param templateVersion
     * @return
     */
    RenderContext createInitHeadRendContext(TemplateVersion templateVersion);

    /**
     * 创建初始化模板尾部渲染上下文
     *
     * @param templateVersion
     * @return
     */
    RenderContext createInitFootRendContext(TemplateVersion templateVersion);

    /**
     * 创建初始化页面原型的上下文
     *
     * @param pagePrototype
     * @return
     */
    RenderContext createInitPagePrototypeRenderContext(PagePrototype pagePrototype);

    /**
     * 创建DEBUG页面原型上下文
     *
     * @param pagePrototypeId
     * @return
     */
    RenderContext createDebugPagePrototypeRenderContext(int pagePrototypeId, Map<String, Object> context);

    /**
     * 创建渲染网站头部的上下文
     *
     * @param shopId
     * @param context
     * @return
     */
    RenderContext createHeadRenderContext(int shopId, Map<String, Object> context);

    /**
     * 创建渲染网站尾部的上下文
     *
     * @param shopId
     * @param context
     * @return
     */
    RenderContext createFootRenderContext(int shopId, Map<String, Object> context);

    /**
     * 创建编辑模式的渲染上下文
     *
     * @param moduleInstanceId
     * @param prototypeId
     * @param context
     * @param renderArea
     * @return
     */
    RenderContext createEditRenderCommonModuleContext(String moduleInstanceId, int prototypeId, Map<String, Object> context, RenderConstants.RenderArea renderArea);


    /**
     * 创建全局模块渲染上下文,context中必须要有当前上下文店铺
     *
     * @param moduleName
     * @param context
     * @return
     */
    RenderContext createGlobalCommonModuleRenderContext(String moduleName, Map<String, Object> context);
}
