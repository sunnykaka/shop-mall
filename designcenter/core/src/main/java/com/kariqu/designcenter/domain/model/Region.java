package com.kariqu.designcenter.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 页面坑，用来放置模块
 * 可在坑里随意添加模块和删除模块
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-15 下午07:16:48
 */
public class Region implements Serializable {

    private static final long serialVersionUID = 6638320144588143847L;

    private String name;

    /**
     * 坑内模块
     */
    private List<Module> modules = new ArrayList<Module>();

    public Region(String regionName) {
        this.name = regionName;
    }

    public Region(String regionName, List<Module> modules) {
        this.name = regionName;
        this.modules = modules;
    }

    /**
     * 增加模块
     * @param module
     */
    public void addModule(Module module) {
        this.modules.add(module);
    }

    public String getName() {
        return name;
    }

    public Module getModule(String moduleInstanceId) {
        for (Module module : modules) {
            if (module.getModuleInstanceId().equals(moduleInstanceId))
                return module;
        }
        return null;
    }

    public List<Module> getModules() {
        return modules;
    }

    /**
     * 删除模块
     *
     * @param moduleInstanceId
     */
    public void deleteModule(String moduleInstanceId) {
        Iterator<Module> iterator = getModules().iterator();
        while (iterator.hasNext()) {
            Module next = iterator.next();
            if (next.getModuleInstanceId().equals(moduleInstanceId)) {
                iterator.remove();
            }
        }
    }

    /**
     * 移动模块
     *
     * @param moduleInstanceId
     * @param direction
     */
    public void moveModules(String moduleInstanceId, String direction) {
        if (modules.size() >= 2) {
            for (int i = 0; i < modules.size(); i++) {
                Module module = modules.get(i);
                if (module.getModuleInstanceId().equals(moduleInstanceId) && "up".equalsIgnoreCase(direction)) {
                    if (i >= 1) {
                        Module previousOne = modules.get(i - 1);
                        modules.set(i - 1, module);
                        modules.set(i, previousOne);
                    }
                    break;
                }
                if (module.getModuleInstanceId().equals(moduleInstanceId) && "down".equalsIgnoreCase(direction)) {
                    if (i <= modules.size() - 2) {
                        Module afterOne = modules.get(i + 1);
                        modules.set(i, afterOne);
                        modules.set(i + 1, module);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Region other = (Region) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
