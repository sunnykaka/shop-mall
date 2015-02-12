package com.kariqu.designcenter.domain.model;

import com.kariqu.designcenter.domain.model.prototype.CommonModule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 页面结构对象，我们页面由模块组成，模块又有自己的参数，所以需要这个对象来对页面进行描述
 * 通过这个对象我们可以知道这个页面有什么模块，模块在什么位置，页面有多少个坑，坑的位置等
 * 每个页面对应一个 PageStructure，页面的渲染直接通过PageStructure中的数据就可以完成，我们在产品环境会缓存这个对象
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-15 下午07:13:10
 */
public class PageStructure implements Serializable {

    private static final long serialVersionUID = -5836896855500174730L;

    //店铺页面的ID
    private long pageId;

    //模板页面原型的ID
    private int prototypeId;

    private Map<String, Region> headRegions = new HashMap<String, Region>();

    private Map<String, Region> bodyRegions = new HashMap<String, Region>();

    private Map<String, Region> footRegions = new HashMap<String, Region>();

    /**
     * 得到这个PageStructure的所有公共模块
     *
     * @return
     */
    public List<CommonModule> getAllCommonModule() {
        List<CommonModule> moduleList = new LinkedList<CommonModule>();
        List<Module> allModules = getAllModules();
        for (Module module : allModules) {
            if (!module.isTemplateModule()) {
                moduleList.add((CommonModule) module.getModulePrototype());
            }
        }
        return moduleList;
    }


    public void addHeadRegion(Region region) {
        this.headRegions.put(region.getName(), region);
    }

    public void addFootRegion(Region region) {
        this.footRegions.put(region.getName(), region);
    }

    public void addBodyRegion(Region region) {
        this.bodyRegions.put(region.getName(), region);
    }

    public void addHeadRegions(Map<String, Region> headRegions) {
        this.headRegions.putAll(headRegions);
    }

    public void addFootRegions(Map<String, Region> footRegions) {
        this.footRegions.putAll(footRegions);
    }

    public void addBodyRegions(Map<String, Region> bodyRegions) {
        this.bodyRegions.putAll(bodyRegions);
    }

    public void addHeadFixedRegionModule(Module module) {
        addFixedModules(module, headRegions);
    }

    public void addFootFixedRegionModule(Module module) {
        addFixedModules(module, footRegions);

    }

    public void addBodyFixedRegionModule(Module module) {
        addFixedModules(module, bodyRegions);
    }

    private void addFixedModules(Module module, Map<String, Region> regions) {
        Region region = regions.get(RenderConstants.FIXED_REGION_NAME);
        if (region == null) {
            Region fixedRegion = new Region(RenderConstants.FIXED_REGION_NAME);
            fixedRegion.addModule(module);
            regions.put(RenderConstants.FIXED_REGION_NAME, fixedRegion);
        } else {
            region.addModule(module);
        }

    }

    public Module getFixedHeadModule(String moduleInstanceId) {
        return headRegions.get(RenderConstants.FIXED_REGION_NAME).getModule(moduleInstanceId);
    }

    public Module getFixedBodyModule(String moduleInstanceId) {
        return bodyRegions.get(RenderConstants.FIXED_REGION_NAME).getModule(moduleInstanceId);
    }

    public Module getFixedFootModule(String moduleInstanceId) {
        return footRegions.get(RenderConstants.FIXED_REGION_NAME).getModule(moduleInstanceId);
    }

    public List<Module> getModulesFromRegionOfHead(String regionName) {
        return headRegions.get(regionName).getModules();
    }

    public List<Module> getModulesFromRegionOfBody(String regionName) {
        return bodyRegions.get(regionName).getModules();
    }

    public List<Module> getModulesFromRegionOfFoot(String regionName) {
        return footRegions.get(regionName).getModules();
    }

    public List<Module> getAllHeadModules() {
        List<Module> result = new LinkedList<Module>();
        for (Region region : headRegions.values()) {
            result.addAll(region.getModules());
        }
        return result;
    }

    public List<Module> getAllFootModules() {
        List<Module> result = new LinkedList<Module>();
        for (Region region : footRegions.values()) {
            result.addAll(region.getModules());
        }
        return result;
    }

    public List<Module> getAllBodyModules() {
        List<Module> result = new LinkedList<Module>();
        for (Region region : bodyRegions.values()) {
            result.addAll(region.getModules());
        }
        return result;
    }

    public List<Module> getAllModules() {
        List<Module> result = new LinkedList<Module>();
        for (Region region : headRegions.values()) {
            result.addAll(region.getModules());
        }
        for (Region region : footRegions.values()) {
            result.addAll(region.getModules());
        }
        for (Region region : bodyRegions.values()) {
            result.addAll(region.getModules());
        }
        return result;
    }


    /**
     * 根据模块原型名得到这个页面的此原型的所有实例
     *
     * @param name
     * @return
     */
    public List<Module> getModulesByPrototypeName(String name) {
        List<Module> holder = new LinkedList<Module>();
        List<Module> allModules = getAllModules();
        for (Module module : allModules) {
            if (module.getModulePrototype().getName().equals(name)) {
                holder.add(module);
            }
        }
        return holder;
    }

    /**
     * @param moduleInstanceId
     */
    public Module getModuleById(String moduleInstanceId) {
        for (Module module : getAllModules()) {
            if (module.getModuleInstanceId().equals(moduleInstanceId))
                return module;
        }
        return null;
    }

    /**
     * @return the headRegions
     */
    public Map<String, Region> getHeadRegions() {
        return headRegions;
    }

    /**
     * @param headRegions the headRegions to set
     */
    public void setHeadRegions(Map<String, Region> headRegions) {
        this.headRegions = headRegions;
    }

    /**
     * @return the bodyRegions
     */
    public Map<String, Region> getBodyRegions() {
        return bodyRegions;
    }

    /**
     * @param bodyRegions the bodyRegions to set
     */
    public void setBodyRegions(Map<String, Region> bodyRegions) {
        this.bodyRegions = bodyRegions;
    }

    /**
     * @return the footRegions
     */
    public Map<String, Region> getFootRegions() {
        return footRegions;
    }

    /**
     * @param footRegions the footRegions to set
     */
    public void setFootRegions(Map<String, Region> footRegions) {
        this.footRegions = footRegions;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public int getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(int prototypeId) {
        this.prototypeId = prototypeId;
    }
}
