package com.kariqu.velocity;

import com.kariqu.designcenter.client.domain.model.RenderContext;
import com.kariqu.designcenter.domain.model.RenderConstants;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

/**
 * 自定义Velocity指令： include_common_module(name,version,domId)
 *
 * @author Tiger
 * @version 1.0
 * @since 2011-1-12 下午04:42:57
 */
public class IncludeCommonModule extends Directive {

    public static final String DIRECTIVE_NAME = "include_common_module";

    @Override
    public String getName() {
        return DIRECTIVE_NAME;
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException,
            ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        Node moduleNameNode = node.jjtGetChild(0);
        Node versionNode = node.jjtGetChild(1);
        Node domIdNode = node.jjtGetChild(2);
        if (moduleNameNode != null && domIdNode != null) {
            String moduleName = String.valueOf(moduleNameNode.value(context));
            String version = String.valueOf(versionNode.value(context));
            int domId = Integer.parseInt(String.valueOf(domIdNode.value(context)));
            RenderContext renderContext = (RenderContext) context.get(RenderConstants.RENDER_CONTEXT);
            writer.write(renderContext.renderCommonModule(moduleName, version, domId));
            return true;
        }
        return false;
    }

}
