package com.kariqu.designcenter.client.infrastructure;

import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.exception.RenderException;
import com.kariqu.designcenter.domain.model.Renderable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.InternalContextAdapterImpl;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Velocity渲染引擎
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-19 下午04:59:55
 */

public class VelocityRenderEngine implements RenderEngine {

    protected final Log logger = LogFactory.getLog(VelocityRenderEngine.class);

    private RuntimeInstance runtimeInstance = new RuntimeInstance();

    {
        runtimeInstance.setProperty("userdirective",new ArrayList<String>(){{
            add("com.kariqu.velocity.IncludeTemplateModule");
            add("com.kariqu.velocity.IncludeRegion");
            add("com.kariqu.velocity.IncludeCommonModule");
        }});
    }

    @Override
    public String render(Renderable renderable, Map<String, Object> renderContext) {
        Map<String, Object> mergedContext = new HashMap<String, Object>();
        mergedContext.putAll(renderContext);
        mergedContext.putAll(renderable.getContext());
        return render(renderable.getContent(), mergedContext);
    }

    @Override
    public String render(String content, Map<String, Object> renderContext) {
        try {
            if (StringUtils.isBlank(content))
                return "";
            return render(parseTemplate(new StringReader(content)), renderContext);
        } catch (ParseException e) {
            logger.error("解析VM异常", e);
            throw new RenderException("渲染VM时出现错误:" + e.getMessage());
        } catch (Exception e) {
            logger.error("渲染时发现未知异常", e);
            throw new RenderException("渲染VM时出现错误:" + e.getClass().getName() + ",msg:" + e.getMessage());
        }
    }


    private SimpleNode parseTemplate(Reader reader) throws ParseException {
        return runtimeInstance.parse(reader, "krq");
    }

    private String render(SimpleNode simpleNode, Map<String, Object> renderContext) throws Exception {
        InternalContextAdapterImpl ica = new InternalContextAdapterImpl(new VelocityContext(renderContext));
        simpleNode.init(ica, runtimeInstance);
        StringWriter writer = new StringWriter();
        simpleNode.render(ica, writer);
        return writer.toString();
    }

}
