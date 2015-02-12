package com.kariqu.designcenter.service.impl;

import com.kariqu.designcenter.domain.model.*;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.Parameter;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;
import com.kariqu.designcenter.repository.ModuleInstanceParamRepository;
import com.kariqu.designcenter.service.CommonModuleService;
import com.kariqu.designcenter.service.ModuleInstanceParamService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-7 下午01:08:23
 */

@Transactional
public class ModuleInstanceParamServiceImpl implements ModuleInstanceParamService {


    @Autowired
    private ModuleInstanceParamRepository moduleInstanceParamRepository;

    @Autowired
    private CommonModuleService commonModuleService;


    /**
     * 实例化页面body部分的参数
     *
     * @param shopPage
     * @return
     */
    public void instanceShopPageParam(ShopPage shopPage) {
        List<ModuleInstanceParam> pageParams = new LinkedList<ModuleInstanceParam>();
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopPage
                .getEditConfigContent());
        for (Module module : pageStructure.getAllModules()) {
            if (!module.isTemplateModule()) {
                CommonModule commonModule = (CommonModule) module.getModulePrototype();
                if (commonModule != null && commonModule.isGlobalGranularity()) {
                    List<ModuleInstanceParam> params = queryModuleParamsByModuleInstanceId(module.getModuleInstanceId());
                    if (CollectionUtils.isEmpty(params)) {
                        List<ModuleInstanceParam> moduleParams = module.getParams();
                        for (ModuleInstanceParam param : moduleParams) {
                            param.setShopId(shopPage.getShopId());
                        }
                        pageParams.addAll(moduleParams);
                    }
                    continue;
                }
            }

            List<ModuleInstanceParam> moduleParams = module.getParams();
            for (ModuleInstanceParam param : moduleParams) {
                param.setShopId(shopPage.getShopId());
                param.setPageId(shopPage.getId());
                param.setParamType(ParamType.BODY);
            }
            pageParams.addAll(moduleParams);
        }
        createModuleInstanceParams(pageParams);
    }

    /**
     * 实例化店铺头部的参数
     *
     * @param shopTemplate
     * @return
     */
    public void instanceShopHeadParams(ShopTemplate shopTemplate) {
        PageStructure headStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate
                .getEditHeadConfigContent());
        List<Module> allModules = headStructure.getAllModules();
        List<ModuleInstanceParam> headParams = new LinkedList<ModuleInstanceParam>();
        for (Module module : allModules) {
            if (!module.isTemplateModule()) {
                CommonModule commonModule = (CommonModule) module.getModulePrototype();
                if (commonModule != null && commonModule.isGlobalGranularity()) {
                    List<ModuleInstanceParam> params = queryModuleParamsByModuleInstanceId(module.getModuleInstanceId());
                    if (CollectionUtils.isEmpty(params)) {
                        List<ModuleInstanceParam> moduleParams = module.getParams();
                        for (ModuleInstanceParam param : moduleParams) {
                            param.setShopId(shopTemplate.getShopId());
                            headParams.add(param);
                        }
                    }
                    continue;
                }
            }
            for (ModuleInstanceParam param : module.getParams()) {
                param.setParamType(ParamType.HEAD);
                param.setShopId(shopTemplate.getShopId());
                headParams.add(param);
            }
        }

        createModuleInstanceParams(headParams);
    }

    /**
     * 实例化店铺尾部的参数
     *
     * @param shopTemplate
     */
    public void instanceShopFootParams(ShopTemplate shopTemplate) {
        PageStructure footStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate
                .getEditFootConfigContent());
        List<Module> allModules = footStructure.getAllModules();
        List<ModuleInstanceParam> footParams = new LinkedList<ModuleInstanceParam>();
        for (Module module : allModules) {
            if (!module.isTemplateModule()) {
                CommonModule commonModule = (CommonModule) module.getModulePrototype();
                if (commonModule != null && commonModule.isGlobalGranularity()) {
                    List<ModuleInstanceParam> params = queryModuleParamsByModuleInstanceId(module.getModuleInstanceId());
                    if (CollectionUtils.isEmpty(params)) {
                        List<ModuleInstanceParam> moduleParams = module.getParams();
                        for (ModuleInstanceParam param : moduleParams) {
                            param.setShopId(shopTemplate.getShopId());
                            footParams.add(param);
                        }
                    }
                    continue;
                }
            }
            for (ModuleInstanceParam param : module.getParams()) {
                param.setParamType(ParamType.FOOT);
                param.setShopId(shopTemplate.getShopId());
                footParams.add(param);
            }
        }
        createModuleInstanceParams(footParams);
    }


    @Override
    public List<ModuleInstanceParam> queryModuleParamsByModuleInstanceId(String moduleInstanceId) {
        List<ModuleInstanceParam> paramList = moduleInstanceParamRepository.queryModuleParamsByModuleInstanceId(moduleInstanceId);
        if (paramList == null)
            return new ArrayList<ModuleInstanceParam>();
        return paramList;
    }

    @Override
    public void createModuleInstanceParam(ModuleInstanceParam moduleInstanceParam) {
        moduleInstanceParamRepository.createModuleInstanceParam(moduleInstanceParam);
    }

    @Override
    public void createModuleInstanceParams(List<ModuleInstanceParam> params) {
        for (ModuleInstanceParam param : params)
            moduleInstanceParamRepository.createModuleInstanceParam(param);
    }


    @Override
    public void deletePageParamsByPageId(long id) {
        moduleInstanceParamRepository.deletePageParamsByPageId(id);
    }

    @Override
    public void deleteShopHeadParamByShopId(int shopId) {
        moduleInstanceParamRepository.deleteShopHeadParamByShopId(shopId);
    }

    @Override
    public void deleteShopFootParamByShopId(int shopId) {
        moduleInstanceParamRepository.deleteShopFootParamByShopId(shopId);
    }

    @Override
    public void deleteModuleInstanceParamOfSingleModule(String moduleInstanceId) {
        moduleInstanceParamRepository.deleteModuleInstanceParamOfSingleModule(moduleInstanceId);
    }


    @Override
    public void deleteShopHeadAndFootParamByShopId(int shopId) {
        moduleInstanceParamRepository.deleteShopHeadAndFootParamByShopId(shopId);
    }


    @Override
    public void updateModuleInstanceParam(ModuleInstanceParam moduleInstanceParam) {
        moduleInstanceParamRepository.updateModuleInstanceParam(moduleInstanceParam);
    }


    @Override
    public void updateModuleInstanceParams(List<ModuleInstanceParam> params) {
        for (ModuleInstanceParam param : params)
            updateModuleInstanceParam(param);
    }

    @Override
    @Transactional
    public void updateModuleInstanceParams(String moduleInstanceId, Map<String, String> requestParameterMap, int modulePrototypeId) {
        List<ModuleInstanceParam> params = this.queryModuleParamsByModuleInstanceId(moduleInstanceId);
        Map<String, ModuleInstanceParam> paramMap = new HashMap<String, ModuleInstanceParam>();
        for (ModuleInstanceParam param : params) {
            paramMap.put(param.getParamName(), param);
        }
        List<ModuleInstanceParam> waitToAddParams = new LinkedList<ModuleInstanceParam>();
        List<ModuleInstanceParam> waitToUpdateParams = new LinkedList<ModuleInstanceParam>();
        List<ModuleInstanceParam> waitToDeleteParams = new LinkedList<ModuleInstanceParam>();
        CommonModule commonModule = commonModuleService.getCommonModuleById(modulePrototypeId);
        List<Parameter> configParams = commonModule.getParams();
        Map<String, Parameter> configParamMap = new HashMap<String, Parameter>();
        for (Parameter configParam : configParams) {
            configParamMap.put(configParam.getName(), configParam);
        }
        for (ModuleInstanceParam param : params) {
            String value = requestParameterMap.get(param.getParamName());
            if (!requestParameterMap.containsKey(param.getParamName())) {
                waitToDeleteParams.add(param);
            } else {
                param.setParamValue(value);
                waitToUpdateParams.add(param);
            }

        }
        ModuleInstanceParamDetail paramIdDetail = ModuleInstanceIdFactory.analyzeModuleInstanceParam(moduleInstanceId);
        for (String name : requestParameterMap.keySet()) {
            if (!paramMap.containsKey(name) && configParamMap.containsKey(name))
                if (paramIdDetail != null) {
                    if (paramIdDetail.isGlobal()) {
                        ModuleInstanceParam newParam = new ModuleInstanceParam();
                        newParam.setParamName(name);
                        newParam.setParamValue(requestParameterMap.get(name));
                        newParam.setShopId(paramIdDetail.getShopId());
                        newParam.setModulePrototypeId(paramIdDetail.getPrototypeId());
                        newParam.setModuleInstanceId(moduleInstanceId);
                        waitToAddParams.add(newParam);
                        continue;
                    }

                    if (paramIdDetail.isHead()) {
                        ModuleInstanceParam newParam = new ModuleInstanceParam();
                        newParam.setParamName(name);
                        newParam.setParamValue(requestParameterMap.get(name));
                        newParam.setShopId(paramIdDetail.getShopId());
                        newParam.setParamType(ParamType.HEAD);
                        newParam.setModulePrototypeId(commonModule.getId());
                        newParam.setModuleInstanceId(moduleInstanceId);
                        waitToAddParams.add(newParam);
                        continue;
                    }
                    if (paramIdDetail.isFoot()) {
                        ModuleInstanceParam newParam = new ModuleInstanceParam();
                        newParam.setParamName(name);
                        newParam.setParamValue(requestParameterMap.get(name));
                        newParam.setShopId(paramIdDetail.getShopId());
                        newParam.setParamType(ParamType.FOOT);
                        newParam.setModulePrototypeId(commonModule.getId());
                        newParam.setModuleInstanceId(moduleInstanceId);
                        waitToAddParams.add(newParam);
                        continue;
                    }

                    if (paramIdDetail.isBody()) {
                        ModuleInstanceParam newParam = new ModuleInstanceParam();
                        newParam.setParamName(name);
                        newParam.setParamValue(requestParameterMap.get(name));
                        newParam.setShopId(paramIdDetail.getShopId());
                        newParam.setParamType(ParamType.BODY);
                        newParam.setPageId(paramIdDetail.getPageId());
                        newParam.setModulePrototypeId(commonModule.getId());
                        newParam.setModuleInstanceId(moduleInstanceId);
                        waitToAddParams.add(newParam);
                    }


                }
        }
        this.updateModuleInstanceParams(waitToUpdateParams);
        this.createModuleInstanceParams(waitToAddParams);
        this.deleteModuleInstanceParams(waitToDeleteParams);
    }

    @Override
    public void deleteModuleInstanceParams(List<ModuleInstanceParam> waitToDeleteParams) {
        for (ModuleInstanceParam waitToDeleteParam : waitToDeleteParams) {
            this.deleteModuleInstanceParam(waitToDeleteParam);
        }
    }

    @Override
    public void deleteModuleInstanceParam(ModuleInstanceParam waitToDeleteParam) {
        this.moduleInstanceParamRepository.deleteModuleInstanceParamById(waitToDeleteParam.getId());
    }
}
