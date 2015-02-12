package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.AbstractRenderContext;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.model.RenderConstants;

import java.util.*;

/**
 * 如果渲染场景需要检查DomId则可继承本类
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-17 下午04:45:03
 */
public abstract class DomCheckRenderContext extends AbstractRenderContext {

    private Map<RenderConstants.RenderArea, Set<Integer>> allDomIds = new HashMap<RenderConstants.RenderArea, Set<Integer>>();

    private Map<RenderConstants.RenderArea, Set<Integer>> sameDomIds = new HashMap<RenderConstants.RenderArea, Set<Integer>>();

    protected RenderConstants.RenderArea renderArea;

    public DomCheckRenderContext(RenderEngine renderEngine) {
        super(renderEngine);
    }

    /**
     * 检查domId是否重复
     *
     * @param domId
     * @param renderArea
     * @return
     */
    protected void checkDomId(int domId, RenderConstants.RenderArea renderArea) {
        Set<Integer> areaDomIds = allDomIds.get(renderArea);
        if (areaDomIds == null) {
            areaDomIds = new HashSet<Integer>();
            allDomIds.put(renderArea, areaDomIds);
        }

        Set<Integer> areaSameDomIds = sameDomIds.get(renderArea);
        if (areaSameDomIds == null) {
            areaSameDomIds = new HashSet<Integer>();
            sameDomIds.put(renderArea, areaSameDomIds);
        }
        if (!areaDomIds.add(domId)) {
            areaSameDomIds.add(domId);
        }

    }

    protected boolean isValid() {
        for (Set<Integer> next : sameDomIds.values()) {
            if (!next.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    protected String getDomIdError() {
        if (sameDomIds.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (RenderConstants.RenderArea next : sameDomIds.keySet()) {
            result.append(next);
            result.append(" : ");
            result.append(sameDomIds.get(next));
        }
        return result.toString();
    }

    @Override
    public String doRender() {
        throw new UnsupportedOperationException();
    }

    protected String wrapperModule(String moduleContent) {
        return "<div class=\"e_module\">" + moduleContent + "</div>";
    }
}
