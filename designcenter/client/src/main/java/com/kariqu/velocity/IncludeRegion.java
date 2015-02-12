package com.kariqu.velocity;

import com.kariqu.designcenter.client.domain.model.ModuleInfo;
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
import java.util.ArrayList;
import java.util.List;

/**
 * includeRegion指令 #set($modules =
 * [["moduleName1",domId,version],["moduleName2"],domId];
 * include_region("regionName",$modules)
 *
 * #include_region("regionName",[])
 * 
 * @author Tiger
 * @since 2010-12-20 下午02:31:10
 * @version 1.0
 */
public class IncludeRegion extends Directive {

    public static final String IMPORT_REGION = "include_region";

    @Override
    public String getName() {
	return IMPORT_REGION;
    }

    @Override
    public int getType() {
	return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException,
	    ResourceNotFoundException, ParseErrorException, MethodInvocationException {
	Node regionNameNode = node.jjtGetChild(0);
	Node modulesNode = node.jjtGetChild(1);
	if (regionNameNode != null && modulesNode != null) {
            RenderContext renderContext = (RenderContext) context.get(RenderConstants.RENDER_CONTEXT);
	    String regionName = String.valueOf(regionNameNode.value(context));
	    List<ArrayList> modules = (List<ArrayList>) modulesNode.value(context);
	    List<ModuleInfo> moduleInfos = new ArrayList<ModuleInfo>(modules.size());
	    for (List moduleValue : modules) {
		int size = moduleValue.size();
		String moduleName = (String) moduleValue.get(0);
		if (size == 3) {
		    String version = (String) moduleValue.get(1);
		    int domId = Integer.parseInt(String.valueOf(moduleValue.get(2)));
		    moduleInfos.add(new ModuleInfo(moduleName, version, domId));
		} else if (size == 2) {
		    int domId = Integer.parseInt(String.valueOf(moduleValue.get(1)));
		    moduleInfos.add(new ModuleInfo(moduleName, domId));
		}
	    }

	    writer.write(renderContext.renderRegion(regionName, moduleInfos));
	    return true;
	}
	return false;
    }

}
