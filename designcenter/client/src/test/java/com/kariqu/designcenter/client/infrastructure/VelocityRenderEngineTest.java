package com.kariqu.designcenter.client.infrastructure;

import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.model.Renderable;
import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-6-9
 * Time: 下午7:25
 */
public class VelocityRenderEngineTest {

    private RenderEngine renderEngine = new VelocityRenderEngine();

    @Test
    public void testRenderRenderable() throws Exception {
        Assert.assertEquals("testAsion", renderEngine.render(new Renderable() {
                    @Override
                    public String getContent() {
                        return "test$name";
                    }

                    @Override
                    public Map<String, Object> getContext() {
                        return new HashMap<String, Object>();
                    }
                }, new HashMap<String, Object>() {
            {
                put("name", "Asion");
            }
        }
        ));
    }

    @Test
    public void testRenderVm() throws Exception {
        Assert.assertEquals("testAsion", renderEngine.render("test$name", new HashMap<String, Object>() {
            {
                put("name", "Asion");
            }
        }));
    }
}
