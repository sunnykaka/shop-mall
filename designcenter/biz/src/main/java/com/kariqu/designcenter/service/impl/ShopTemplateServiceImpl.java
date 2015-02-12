package com.kariqu.designcenter.service.impl;

import com.kariqu.common.cache.CacheService;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceIdFactory;
import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;
import com.kariqu.designcenter.repository.ShopTemplateRepository;
import com.kariqu.designcenter.service.CacheKeyFactory;
import com.kariqu.designcenter.service.CommonModuleService;
import com.kariqu.designcenter.service.ModuleInstanceParamService;
import com.kariqu.designcenter.service.ShopTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-7 下午01:04:44
 */

@Transactional
public class ShopTemplateServiceImpl implements ShopTemplateService {

    @Autowired
    private ShopTemplateRepository shopTemplateRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private CommonModuleService commonModuleService;

    @Autowired
    private ModuleInstanceParamService moduleInstanceParamService;

    @Override
    public int createShopTemplate(ShopTemplate shopTemplate) {
        shopTemplateRepository.createShopTemplate(shopTemplate);
        return shopTemplate.getId();
    }


    @Override
    public void deleteShopTemplate(int id) {
        shopTemplateRepository.deleteShopTemplateById(id);
        cacheService.remove(CacheKeyFactory.createShopTemplateCacheKey(id));
        ShopTemplate shopTemplate = getShopTemplateById(id);
        if (shopTemplate != null) {
            cacheService.remove(CacheKeyFactory.createShopTemplateCacheKeyWithShopId(shopTemplate.getShopId()));
        }
    }

    @Override
    public void releaseAllGlobalCommonModule(int shopTemplateId) {
        ShopTemplate shopTemplate = shopTemplateRepository.getShopTemplateById(shopTemplateId);
        List<CommonModule> commonModules = commonModuleService.queryAllGlobalCommonModule();
        PageStructure pageStructure = new PageStructure();
        for (CommonModule commonModule : commonModules) {
            Module module = new Module(ModuleInstanceIdFactory.createModuleIdOfGlobalGranularity(shopTemplate.getShopId(), commonModule.getId()), commonModule);
            /**
             * 此处用PageStructure的结构来存储全局模块的配置，没有模块区域信息
             */
            pageStructure.addHeadFixedRegionModule(module);
        }
        initParams(pageStructure);
        String globalModuleInfoConfig = PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure);
        shopTemplate.setGlobalModuleInfoConfig(globalModuleInfoConfig);
        updateShopTemplate(shopTemplate);
    }


    @Override
    public ShopTemplate getShopTemplateById(int shopTemplateId) {
        String cacheKey = CacheKeyFactory.createShopTemplateCacheKey(shopTemplateId);
        ShopTemplate shopTemplate = (ShopTemplate) cacheService.get(cacheKey);
        if (shopTemplate == null) {
            shopTemplate = shopTemplateRepository.getShopTemplateById(shopTemplateId);
            if (shopTemplate != null) {
                cacheService.put(cacheKey, shopTemplate);
            }
        }
        return shopTemplate;
    }


    @Override
    public void updateShopTemplate(ShopTemplate shopTemplate) {
        if (shopTemplate == null) return;
        shopTemplateRepository.updateShopTemplate(shopTemplate);
        cacheService.remove(CacheKeyFactory.createShopTemplateCacheKey(shopTemplate.getId()));
        cacheService.remove(CacheKeyFactory.createShopTemplateCacheKeyWithShopId(shopTemplate.getShopId()));
    }

    @Override
    public ShopTemplate getShopTemplateByShopId(int shopId) {
        String cacheKey = CacheKeyFactory.createShopTemplateCacheKeyWithShopId(shopId);
        ShopTemplate shopTemplate = (ShopTemplate) cacheService.get(cacheKey);
        if (shopTemplate == null) {
            shopTemplate = shopTemplateRepository.getShopTemplateByShopId(shopId);
            if (shopTemplate != null) {
                cacheService.put(cacheKey, shopTemplate);
            }
        }
        return shopTemplate;
    }

    /**
     * @param pageStructure
     */
    private void initParams(PageStructure pageStructure) {
        for (Module module : pageStructure.getAllModules()) {
            List<ModuleInstanceParam> params = moduleInstanceParamService.queryModuleParamsByModuleInstanceId(module.getModuleInstanceId());
            module.setParams(params);
        }
    }

}
