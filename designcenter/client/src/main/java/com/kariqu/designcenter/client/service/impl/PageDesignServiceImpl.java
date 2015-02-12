package com.kariqu.designcenter.client.service.impl;

import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.service.PageDesignService;
import com.kariqu.designcenter.domain.model.*;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.Parameter;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;
import com.kariqu.designcenter.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 装修服务
 *
 * @author Tiger
 * @version 1.0
 * @since 12-5-7 下午3:51
 */
public class PageDesignServiceImpl implements PageDesignService {

    private final static Log logger = LogFactory.getLog(PageDesignServiceImpl.class);

    @Autowired
    private CommonModuleService commonModuleService;

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private ShopTemplateService shopTemplateService;

    @Autowired
    private ModuleInstanceParamService moduleInstanceParamService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Override
    @Transactional
    public Result addModuleToHead(int prototypeId, int shopTemplateId, String regionName) {
        CommonModule commonModule = commonModuleService.getCommonModuleById(prototypeId);
        if (commonModule == null) {
            String message = "添加的模块原型不存在，模块原型id=" + prototypeId;
            logger.error(message);
            return new Result(false, message, null);
        }
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateById(shopTemplateId);
        if (shopTemplate == null) {
            String message = "店铺模板不存在，店铺模板id=" + shopTemplateId;
            logger.error(message);
            return new Result(false, message, null);
        }
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getEditHeadConfigContent());
        Map<String, Region> headRegions = pageStructure.getHeadRegions();
        Region region = headRegions.get(regionName);
        if (region == null) {
            String message = "坑不存在，坑名称为：" + regionName;
            logger.error(message);
            return new Result(false, message, null);
        }
        String moduleInstanceId;
        moduleInstanceId = generateModuleInstanceId(commonModule, shopTemplate.getShopId());
        Module module = new Module(moduleInstanceId, commonModule);
        region.addModule(module);
        if (commonModule.isGlobalGranularity()) {
            List<ModuleInstanceParam> params = moduleInstanceParamService.queryModuleParamsByModuleInstanceId(moduleInstanceId);
            if (CollectionUtils.isEmpty(params)) {
                List<ModuleInstanceParam> moduleInstanceParams = generateGlobalModuleParams(commonModule, shopTemplate.getShopId(), moduleInstanceId);
                module.addParams(moduleInstanceParams);
                moduleInstanceParamService.createModuleInstanceParams(moduleInstanceParams);
            }
        } else {
            List<ModuleInstanceParam> moduleInstanceParams = generateHeadModuleParams(prototypeId, commonModule, shopTemplate, moduleInstanceId);
            module.addParams(moduleInstanceParams);
            moduleInstanceParamService.createModuleInstanceParams(moduleInstanceParams);
        }
        shopTemplate.setEditHeadConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
        shopTemplateService.updateShopTemplate(shopTemplate);
        Result result = new Result(true, "增加模块成功", null);
        result.addDataEntry("data-url", urlBrokerFactory.getUrl("modulePreview").addQueryData("moduleInstanceId", moduleInstanceId).addQueryData("prototypeId", prototypeId).addQueryData("area", "head").toString());
        return result;
    }

    private String generateModuleInstanceId(CommonModule commonModule, int shopId) {
        String moduleInstanceId;
        if (commonModule.isGlobalGranularity()) {
            moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfGlobalGranularity(shopId, commonModule.getId());
        } else {
            moduleInstanceId = String.valueOf(System.currentTimeMillis());
        }
        return moduleInstanceId;
    }

    private List<ModuleInstanceParam> generateHeadModuleParams(int prototypeId, CommonModule commonModule, ShopTemplate shopTemplate, String moduleInstanceId) {
        List<ModuleInstanceParam> moduleInstanceParams = new LinkedList<ModuleInstanceParam>();
        for (Parameter param : commonModule.getParams()) {
            ModuleInstanceParam moduleInstanceParam = new ModuleInstanceParam();
            moduleInstanceParam.setModuleInstanceId(moduleInstanceId);
            moduleInstanceParam.setParamName(param.getName());
            moduleInstanceParam.setShopId(shopTemplate.getShopId());
            moduleInstanceParam.setParamValue(param.getValue().toString());
            moduleInstanceParam.setParamType(ParamType.HEAD);
            moduleInstanceParam.setModulePrototypeId(prototypeId);
            moduleInstanceParams.add(moduleInstanceParam);
        }
        return moduleInstanceParams;
    }

    private List<ModuleInstanceParam> generateGlobalModuleParams(CommonModule commonModule, int shopId, String moduleInstanceId) {
        List<ModuleInstanceParam> moduleInstanceParams = new LinkedList<ModuleInstanceParam>();
        for (Parameter param : commonModule.getParams()) {
            ModuleInstanceParam moduleInstanceParam = new ModuleInstanceParam();
            moduleInstanceParam.setModuleInstanceId(moduleInstanceId);
            moduleInstanceParam.setParamName(param.getName());
            moduleInstanceParam.setShopId(shopId);
            moduleInstanceParam.setParamValue(param.getValue().toString());
            moduleInstanceParam.setModulePrototypeId(commonModule.getId());
            moduleInstanceParams.add(moduleInstanceParam);
        }
        return moduleInstanceParams;
    }


    @Override
    @Transactional
    public Result addModuleToFoot(int prototypeId, int shopTemplateId, String regionName) {
        CommonModule commonModule = commonModuleService.getCommonModuleById(prototypeId);
        if (commonModule == null) {
            String message = "添加的模块原型不存在，模块原型id=" + prototypeId;
            logger.error(message);
            return new Result(false, message, null);
        }
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateById(shopTemplateId);
        if (shopTemplate == null) {
            String message = "店铺模板不存在，店铺模板id=" + shopTemplateId;
            logger.error(message);
            return new Result(false, message, null);
        }
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getEditFootConfigContent());
        Map<String, Region> footRegions = pageStructure.getFootRegions();
        Region region = footRegions.get(regionName);
        if (region == null) {
            String message = "坑不存在，坑名称为：" + regionName;
            logger.error(message);
            return new Result(false, message, null);
        }
        String moduleInstanceId = generateModuleInstanceId(commonModule, shopTemplate.getShopId());
        Module module = new Module(moduleInstanceId, commonModule);
        if (commonModule.isGlobalGranularity()) {
            List<ModuleInstanceParam> params = moduleInstanceParamService.queryModuleParamsByModuleInstanceId(moduleInstanceId);
            if (CollectionUtils.isEmpty(params)) {
                List<ModuleInstanceParam> moduleInstanceParams = generateGlobalModuleParams(commonModule, shopTemplate.getShopId(), moduleInstanceId);
                module.addParams(moduleInstanceParams);
                moduleInstanceParamService.createModuleInstanceParams(moduleInstanceParams);
            }
        } else {
            List<ModuleInstanceParam> moduleInstanceParams = generateFootModuleParam(commonModule, shopTemplate, moduleInstanceId);
            module.addParams(moduleInstanceParams);
            moduleInstanceParamService.createModuleInstanceParams(moduleInstanceParams);
        }
        region.addModule(module);
        shopTemplate.setEditFootConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
        shopTemplateService.updateShopTemplate(shopTemplate);
        Result result = new Result(true, "增加模块成功", null);
        result.addDataEntry("data-url", urlBrokerFactory.getUrl("modulePreview").addQueryData("moduleInstanceId", moduleInstanceId).addQueryData("prototypeId", prototypeId).addQueryData("area", "foot").toString());
        return result;
    }

    private List<ModuleInstanceParam> generateFootModuleParam(CommonModule commonModule, ShopTemplate shopTemplate, String moduleInstanceId) {
        List<ModuleInstanceParam> moduleInstanceParams = new LinkedList<ModuleInstanceParam>();
        for (Parameter param : commonModule.getParams()) {
            ModuleInstanceParam moduleInstanceParam = new ModuleInstanceParam();
            moduleInstanceParam.setModuleInstanceId(String.valueOf(moduleInstanceId));
            moduleInstanceParam.setParamName(param.getName());
            moduleInstanceParam.setShopId(shopTemplate.getShopId());
            moduleInstanceParam.setParamValue(param.getValue().toString());
            moduleInstanceParam.setParamType(ParamType.FOOT);
            moduleInstanceParam.setModulePrototypeId(commonModule.getId());
            moduleInstanceParams.add(moduleInstanceParam);
        }
        return moduleInstanceParams;
    }

    @Override
    @Transactional
    public Result addModuleToBody(int prototypeId, long pageId, String regionName) {
        CommonModule commonModule = commonModuleService.getCommonModuleById(prototypeId);
        if (commonModule == null) {
            String message = "添加的模块原型不存在，模块原型id=" + prototypeId;
            logger.error(message);
            return new Result(false, message, null);
        }
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        if (shopPage == null) {
            String message = "店铺页面不存在,页面Id=" + pageId;
            logger.error(message);
            return new Result(false, message, null);
        }
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopPage.getEditConfigContent());
        if (pageStructure != null) {
            Map<String, Region> bodyRegions = pageStructure.getBodyRegions();
            Region region = bodyRegions.get(regionName);
            if (region == null) {
                String message = "坑不存在，坑名称为：" + regionName;
                logger.error(message);
                return new Result(false, message, null);
            }
            String moduleInstanceId = generateModuleInstanceId(commonModule, shopPage.getShopId());
            Module module = new Module(String.valueOf(moduleInstanceId), commonModule);
            if (commonModule.isGlobalGranularity()) {
                List<ModuleInstanceParam> params = moduleInstanceParamService.queryModuleParamsByModuleInstanceId(moduleInstanceId);
                if (CollectionUtils.isEmpty(params)) {
                    List<ModuleInstanceParam> moduleInstanceParams = generateGlobalModuleParams(commonModule, shopPage.getShopId(), moduleInstanceId);
                    moduleInstanceParamService.createModuleInstanceParams(moduleInstanceParams);
                    module.addParams(moduleInstanceParams);
                }
            } else {
                List<ModuleInstanceParam> moduleInstanceParams = generateBodyModuleParams(prototypeId, commonModule, shopPage, moduleInstanceId);
                moduleInstanceParamService.createModuleInstanceParams(moduleInstanceParams);
                module.addParams(moduleInstanceParams);
            }
            region.addModule(module);
            shopPage.setEditConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
            shopPageService.updateShopPage(shopPage);
            Result result = new Result(true, "增加模块成功", null);
            result.addDataEntry("data-url", urlBrokerFactory.getUrl("modulePreview").addQueryData("moduleInstanceId", moduleInstanceId).addQueryData("prototypeId", prototypeId).addQueryData("area", "body").toString());
            return result;
        }
        return new Result(false, "添加模块失败，构建页面结构出错", null);
    }

    private List<ModuleInstanceParam> generateBodyModuleParams(int prototypeId, CommonModule commonModule, ShopPage shopPage, String moduleInstanceId) {
        List<ModuleInstanceParam> moduleInstanceParams = new LinkedList<ModuleInstanceParam>();
        for (Parameter param : commonModule.getParams()) {
            ModuleInstanceParam moduleInstanceParam = new ModuleInstanceParam();
            moduleInstanceParam.setModuleInstanceId(moduleInstanceId);
            moduleInstanceParam.setParamName(param.getName());
            moduleInstanceParam.setShopId(shopPage.getShopId());
            moduleInstanceParam.setParamValue(param.getValue().toString());
            moduleInstanceParam.setParamType(ParamType.BODY);
            moduleInstanceParam.setModulePrototypeId(prototypeId);
            moduleInstanceParams.add(moduleInstanceParam);
        }
        return moduleInstanceParams;
    }

    @Override
    @Transactional
    public Result deleteModuleFromHead(String moduleInstanceId, int shopTemplateId, String regionName) {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateById(shopTemplateId);
        if (shopTemplate == null) {
            String message = "店铺模板不存在，店铺模板id=" + shopTemplateId;
            logger.error(message);
            return new Result(false, message, null);
        }
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getEditHeadConfigContent());
        Map<String, Region> headRegions = pageStructure.getHeadRegions();
        Region region = headRegions.get(regionName);
        if (region == null) {
            String message = "坑不存在，坑名称为：" + regionName;
            logger.error(message);
            return new Result(false, message, null);
        }
        deleteModuleInstanceParams(region.getModule(moduleInstanceId));
        region.deleteModule(moduleInstanceId);
        shopTemplate.setEditHeadConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
        shopTemplateService.updateShopTemplate(shopTemplate);
        return new Result(true, "删除模块成功", null);
    }

    @Override
    @Transactional
    public Result deleteModuleFromFoot(String moduleInstanceId, int shopTemplateId, String regionName) {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateById(shopTemplateId);
        if (shopTemplate == null) {
            String message = "店铺模板不存在，店铺模板id=" + shopTemplateId;
            logger.error(message);
            return new Result(false, message, null);
        }
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getEditFootConfigContent());
        Map<String, Region> footRegions = pageStructure.getFootRegions();
        Region region = footRegions.get(regionName);
        if (region == null) {
            String message = "坑不存在，坑名称为：" + regionName;
            logger.error(message);
            return new Result(false, message, null);
        }
        deleteModuleInstanceParams(region.getModule(moduleInstanceId));
        region.deleteModule(moduleInstanceId);
        shopTemplate.setEditFootConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
        shopTemplateService.updateShopTemplate(shopTemplate);
        return new Result(true, "删除模块成功", null);
    }

    @Override
    @Transactional
    public Result deleteModuleFromBody(String moduleInstanceId, long pageId, String regionName) {
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        if (shopPage == null) {
            String message = "店铺页面不存在，页面Id=" + pageId;
            logger.error(message);
            return new Result(false, message, null);
        }
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopPage.getEditConfigContent());
        if (pageStructure != null) {
            Map<String, Region> bodyRegions = pageStructure.getBodyRegions();
            Region region = bodyRegions.get(regionName);
            if (region == null) {
                String message = "坑不存在，坑名称为：" + regionName;
                logger.error(message);
                return new Result(false, message, null);
            }
            deleteModuleInstanceParams(region.getModule(moduleInstanceId));
            region.deleteModule(moduleInstanceId);
            shopPage.setEditConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
            shopPageService.updateShopPage(shopPage);
            return new Result(true, "", null);
        }
        return new Result(false, "删除模块失败“，构建页面结构出错", null);
    }

    /**
     * 删除模块参数，如果是全局模块则不进行模块参数删除
     *
     * @param module
     */
    private void deleteModuleInstanceParams(Module module) {
        if (module == null) {
            return;
        }
        if (module.isTemplateModule()) {
            moduleInstanceParamService.deleteModuleInstanceParamOfSingleModule(module.getModuleInstanceId());
        } else {
            CommonModule commonModule = (CommonModule) module.getModulePrototype();
            if (!commonModule.isGlobalGranularity()) {
                moduleInstanceParamService.deleteModuleInstanceParamOfSingleModule(module.getModuleInstanceId());
            }
        }
    }

    @Override
    @Transactional
    public Result moveModuleOfHead(String moduleInstanceId, int shopTemplateId, String regionName, String direction) {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateById(shopTemplateId);
        if (shopTemplate == null) {
            String message = "店铺模板不存在，店铺模板id=" + shopTemplateId;
            logger.error(message);
            return new Result(false, message, null);
        }
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getEditHeadConfigContent());
        Region region = pageStructure.getHeadRegions().get(regionName);
        if (region == null) {
            String message = "坑不存在，坑名称为：" + regionName;
            logger.error(message);
            return new Result(false, message, null);
        }
        region.moveModules(moduleInstanceId, direction);
        shopTemplate.setEditHeadConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
        shopTemplateService.updateShopTemplate(shopTemplate);
        return new Result(true, "移动模块成功", null);
    }

    @Override
    @Transactional
    public Result moveModuleOfFoot(String moduleInstanceId, int shopTemplateId, String regionName, String direction) {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateById(shopTemplateId);
        if (shopTemplate == null) {
            String message = "店铺模板不存在，店铺模板id=" + shopTemplateId;
            logger.error(message);
            return new Result(false, message, null);
        }
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getEditFootConfigContent());
        Region region = pageStructure.getFootRegions().get(regionName);
        if (region == null) {
            String message = "坑不存在，坑名称为：" + regionName;
            logger.error(message);
            return new Result(false, message, null);
        }
        region.moveModules(moduleInstanceId, direction);
        shopTemplate.setEditFootConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
        shopTemplateService.updateShopTemplate(shopTemplate);
        return new Result(true, "移动模块成功", null);
    }

    @Override
    @Transactional
    public Result moveModuleOfBody(String moduleInstanceId, long pageId, String regionName, String direction) {
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        if (shopPage == null) {
            String message = "店铺页面不存在，页面Id=" + pageId;
            logger.error(message);
            return new Result(false, message, null);
        }
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopPage.getEditConfigContent());
        if (pageStructure != null) {
            Region region = pageStructure.getBodyRegions().get(regionName);
            region.moveModules(moduleInstanceId, direction);
            shopPage.setEditConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
            shopPageService.updateShopPage(shopPage);
        }

        return new Result(true, "移动模块成功", null);
    }

}
