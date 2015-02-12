package com.kariqu.designcenter.service.impl;

import com.kariqu.designcenter.domain.exception.ModuleConfigException;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.util.ModuleParamAndXmlConverter;
import com.kariqu.designcenter.domain.util.RenderUtil;
import com.kariqu.designcenter.repository.CommonModuleRepository;
import com.kariqu.designcenter.service.CommonModuleService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-7 下午01:07:52
 */

public class CommonModuleServiceImpl implements CommonModuleService {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private CommonModuleRepository commonModuleRepository;

    private boolean developMode = false;

    private String localCommonModulePath;

    /**
     * 发布的时候拷贝字段
     * 同时将模块最新的配置解析到属性文件中
     *
     * @param id
     */
    @Override
    @Transactional
    public void releaseCommonModule(int id) {
        CommonModule currentCommonModule = getCommonModuleById(id);
        currentCommonModule.setFormContent(currentCommonModule.getEditFormContent());
        currentCommonModule.setLogicCode(currentCommonModule.getEditLogicCode());
        currentCommonModule.setModuleContent(currentCommonModule.getEditModuleContent());
        currentCommonModule.setModuleJs(currentCommonModule.getEditModuleJs());
        try {
            currentCommonModule.setConfig(ModuleParamAndXmlConverter.readModuleConfig(currentCommonModule.getModuleConfig()));
        } catch (ModuleConfigException e) {
            logger.error("模块的配置有错:[" + currentCommonModule.getModuleConfig() + "]", e);
            currentCommonModule.setConfig("");
        }
        updateCommonModule(currentCommonModule);
    }


    @Override
    @Transactional
    public int createCommonModule(CommonModule commonModule) {
        try {
            commonModule.setConfig(ModuleParamAndXmlConverter.readModuleConfig(commonModule.getModuleConfig()));
        } catch (ModuleConfigException e) {
            logger.error("模块的配置有错:[" + commonModule.getModuleConfig() + "]", e);
            commonModule.setConfig("");
        }
        commonModuleRepository.createCommonModule(commonModule);
        return commonModule.getId();
    }

    @Override
    public void updateCommonModule(CommonModule commonModule) {
        commonModuleRepository.updateCommonModule(commonModule);
    }

    @Override
    public void deleteCommonModule(int id) {
        commonModuleRepository.deleteCommonModuleById(id);
    }

    @Override
    public List<CommonModule> queryAllCommonModule() {
        return commonModuleRepository.queryAllCommonModules();
    }

    @Override
    public boolean existCommonModule(String name) {
        return commonModuleRepository.queryCommonModuleByName(name) != null;
    }

    /**
     * 通过名字和版本加载系统模块
     *
     * @param name
     * @param version
     * @return
     */
    @Override
    public CommonModule queryCommonModuleByNameAndVersion(String name, String version) {
        CommonModule commonModule = commonModuleRepository.queryCommonModuleByNameAndVersion(name, version);
        return commonModule;
    }

    /**
     * 通过名字和版本加载系统模块，如果配置了开发模式则从文件系统中读取
     *
     * @param name
     * @param version
     * @return
     */
    @Override
    public CommonModule getCommonModuleForRendering(String name, String version) {
        if (logger.isDebugEnabled()) {
            logger.debug("开始加载模块，开发者模式是" + developMode);
        }
        CommonModule commonModule = queryCommonModuleByNameAndVersion(name, version);
        if (commonModule != null && developMode && StringUtils.isNotBlank(localCommonModulePath)) {
            File moduleDir = new File(localCommonModulePath, commonModule.getName());
            if (moduleDir.exists()) {
                return RenderUtil.readModuleDir(moduleDir, commonModule);
            }
            logger.error(String.format("从(%s)中加载不到文件(%s)对应的模块(%s), 请检查 filter_DC 里 localCommonModulePath 项是否有配置",
                    localCommonModulePath, moduleDir.getAbsolutePath(), commonModule.getName()));
        }
        return commonModule;
    }

    @Override
    public List<CommonModule> queryAllGlobalCommonModule() {
        return commonModuleRepository.queryAllGlobalCommonModules();
    }

    @Override
    public CommonModule getCommonModuleById(int id) {
        return commonModuleRepository.getCommonModuleById(id);
    }

    public void setLocalCommonModulePath(String localCommonModulePath) {
        this.localCommonModulePath = localCommonModulePath;
    }

    public void setDevelopMode(boolean developMode) {
        this.developMode = developMode;
    }
}
