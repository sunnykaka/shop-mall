package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.util.RenderUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tiger
 * @version 1.0.0
 * @since 11-8-4 下午3:23
 */

public class CommonModuleContainer implements InitializingBean {

    private Log logger = LogFactory.getLog(CommonModuleContainer.class);

    /**
     * 用一个并发hashmap来缓存系统模块
     */
    private ConcurrentHashMap<Integer, CommonModule> commonModules = new ConcurrentHashMap<Integer, CommonModule>();

    @Autowired
    private CommonModuleService commonModuleService;

    @Autowired
    private ModuleContextExecutor moduleContextExecutor;

    private boolean developMode = false;

    private String localCommonModulePath;

    public List<CommonModule> queryCommonModulesByIds(List<Integer> ids) {
        List<CommonModule> result = new LinkedList<CommonModule>();
        for (Integer id : ids) {
            result.add(getCommonModuleById(id));
        }
        return result;
    }


    public void reset(List<CommonModule> commonModules) {
        if (null == commonModules) {
            return;
        }
        for (CommonModule commonModule : commonModules) {
            this.commonModules.put(commonModule.getId(), commonModule);
            moduleContextExecutor.resetModuleScript(commonModule);
        }

    }

    public CommonModule resetSingle(CommonModule commonModule) {
        moduleContextExecutor.resetModuleScript(commonModule);
        return commonModules.put(commonModule.getId(), commonModule);
    }

    public boolean reloadAll() {
        try {
            List<CommonModule> commonModuleList = commonModuleService.queryAllCommonModule();
            for (CommonModule commonModule : commonModuleList) {
                moduleContextExecutor.resetModuleScript(commonModule);
                this.commonModules.put(commonModule.getId(), commonModule);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("重新加载了一次公共模块");
            }
            return true;
        } catch (Exception e) {
            String msg = "加载所有系统模块出错";
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public Collection<CommonModule> queryAllCommonModules() {
        return this.commonModules.values();
    }

    public CommonModule getCommonModuleById(int prototypeId) {
        if (logger.isDebugEnabled()) {
            logger.debug("在客户端中加载模块,开发模式是" + developMode);
        }
        CommonModule commonModule = commonModules.get(prototypeId);
        if (commonModule == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("在客户端中没找到模块，即将从数据库中加载，id是" + prototypeId);
            }
            commonModule = commonModuleService.getCommonModuleById(prototypeId);
            if (commonModule == null) {
                logger.error("在公共模块客户端中没找到模块，同时数据库中也没有找到，ID是:" + prototypeId);
                throw new ModuleNotFoundException("在公共模块客户端中没找到模块，同时数据库中也没有找到，ID是:" + prototypeId);
            }
            commonModules.put(commonModule.getId(), commonModule);
        }
        if (developMode && StringUtils.isNotBlank(localCommonModulePath)) {
            File moduleDir = new File(localCommonModulePath, commonModule.getName());
            if (moduleDir.exists()) {
                return RenderUtil.readModuleDir(moduleDir, commonModule);
            }

            logger.error(String.format("从(%s)中加载不到文件(%s)对应的模块(%s), 请检查 filter_DC 里 localCommonModulePath 项是否配置正确",
                    localCommonModulePath, moduleDir.getAbsolutePath(), commonModule.getName()));
            return commonModule;
        }
        return commonModule;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        reloadAll();
    }

    public void setDevelopMode(boolean developMode) {
        this.developMode = developMode;
    }

    public void setLocalCommonModulePath(String localCommonModulePath) {
        this.localCommonModulePath = localCommonModulePath;
    }

    public CommonModule getCommonModuleByName(String moduleName) {
        for (CommonModule commonModule : commonModules.values()) {
            if (commonModule.getName().equals(moduleName)) {
                return commonModule;
            }
        }
        return null;
    }
}
