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
 * 自定义Velocity include_module(moduleName,domId)
 * 
 * @author Tiger
 * @since 2010-12-19 下午04:23:54
 * @version 1.0
 */
public class IncludeTemplateModule extends Directive {

    public static final String DIRECTIVE_NAME = "include_template_module";

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
	Node domIdNode = node.jjtGetChild(1);
	if (moduleNameNode != null && domIdNode != null) {
            RenderContext renderContext = (RenderContext) context.get(RenderConstants.RENDER_CONTEXT);
	    String moduleName = String.valueOf(moduleNameNode.value(context));
	    int domId = Integer.parseInt(String.valueOf(domIdNode.value(context)));
	    writer.write(renderContext.renderTemplateModule(moduleName, domId));
	    return true;
	}
	return false;
    }
}
