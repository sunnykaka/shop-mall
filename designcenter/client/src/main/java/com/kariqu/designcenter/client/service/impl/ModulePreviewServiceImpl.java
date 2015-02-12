package com.kariqu.designcenter.client.service.impl;

import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.client.service.ModulePreviewService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.service.CommonModuleService;
import com.kariqu.designcenter.service.ModuleContextExecutor;
import com.kariqu.productcenter.service.ProductPictureResolver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-26
 *        Time: 下午3:04
 */
public class ModulePreviewServiceImpl implements ModulePreviewService {

    @Autowired
    private CommonModuleService commonModuleService;

    @Autowired
    private RenderEngine renderEngine;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private ModuleContextExecutor moduleContextExecutor;

    @Autowired
    private ProductPictureResolver productPictureResolver;

    private Map<String, Object> prodOpenApiContext;

    @Override
    public String previewCommonModule(String name, Map<String, Object> context) {
        CommonModule commonModule = commonModuleService.getCommonModuleForRendering(name, "1.0");
        if (commonModule == null) {
            return "没有找到模块";
        }
        context.put(RenderConstants.URL_Broker, urlBrokerFactory);
        context.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        context.putAll(prodOpenApiContext);
        context.put(RenderConstants.RENDER_MOD, RenderConstants.RenderMod.debug);

        Map<String, Object> map = moduleContextExecutor.executeForDebug(context, commonModule);
        if (map != null) {
            context.putAll(map);
        }
        return renderEngine.render(commonModule, context);
    }

    public Map<String, Object> getProdOpenApiContext() {
        return prodOpenApiContext;
    }

    public void setProdOpenApiContext(Map<String, Object> prodOpenApiContext) {
        this.prodOpenApiContext = prodOpenApiContext;
    }
}
