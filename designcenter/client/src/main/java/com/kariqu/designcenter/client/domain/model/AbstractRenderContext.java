package com.kariqu.designcenter.client.domain.model;

import com.kariqu.designcenter.domain.exception.RenderException;
import com.kariqu.designcenter.domain.model.RenderConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 抽象的渲染上下文
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-17 下午04:45:03
 */
public abstract class AbstractRenderContext extends ContextHolder implements RenderContext {

    protected final Log logger = LogFactory.getLog(AbstractRenderContext.class);


    /**
     * 渲染引擎
     */
    protected RenderEngine renderEngine;


    public AbstractRenderContext(RenderEngine renderEngine) {
        this.renderEngine = renderEngine;
        this.globalContext.put(RenderConstants.RENDER_CONTEXT, this);
    }

    @Override
    public String render() {
        try {
            return doRender();
        } catch (RenderException e) {
            logger.error("渲染页面异常" + e.getMessage(), e);
            throw e;
        }
    }


    protected abstract String doRender();


    protected String wrapperRegion(String originalContent) {
        return "<div class=\"e_region\">" + originalContent + "</div>";
    }
}
