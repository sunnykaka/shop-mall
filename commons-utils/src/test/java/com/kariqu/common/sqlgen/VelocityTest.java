package com.kariqu.common.sqlgen;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.InternalContextAdapterImpl;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Asion
 * Date: 12-3-7
 * Time: 下午3:42
 */
public class VelocityTest {
    
    @Test
    public void test() throws ParseException, IOException {
        RuntimeInstance runtimeInstance = new RuntimeInstance();
        SimpleNode simpleNode = runtimeInstance.parse("hello${table}", "test");
        Map<String, Object> mergedContext = new HashMap<String, Object>();
        mergedContext.put("table", "tttttt");
        InternalContextAdapterImpl ica = new InternalContextAdapterImpl(new VelocityContext(mergedContext));
        simpleNode.init(ica, runtimeInstance);
        StringWriter writer = new StringWriter();
        simpleNode.render(ica, writer);
        System.out.println(writer.toString());
    }

}
