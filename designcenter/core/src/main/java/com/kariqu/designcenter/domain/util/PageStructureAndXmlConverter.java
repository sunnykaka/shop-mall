package com.kariqu.designcenter.domain.util;

import com.kariqu.designcenter.domain.model.*;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.ModuleGranularity;
import com.kariqu.designcenter.domain.model.prototype.ModulePrototype;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PageStructure和页面配置文件转换器
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-10 下午04:15:03
 */
public class PageStructureAndXmlConverter {

    protected static final Log logger = LogFactory.getLog(PageStructureAndXmlConverter.class);

    public static PageStructure convertXmlToPageStructure(String configContent) {
        Document document;
        PageStructure pageStructure = new PageStructure();
        try {
            document = DocumentHelper.parseText(configContent);
        } catch (DocumentException e) {
            logger.error("解析页面结构文件出错", e);
            return null;
        } catch (Exception e) {
            logger.error("解析页面结构文件出错", e);
            return null;
        }
        Element root = document.getRootElement();

        Node pageNode = root.selectSingleNode("/page");
        String pageId = pageNode.valueOf("@pageId");
        if (!pageId.equals("")) {
            pageStructure.setPageId(Long.parseLong(pageId));
        }
        String prototypeId = pageNode.valueOf("@prototypeId");
        if (!prototypeId.equals("")) {
            pageStructure.setPrototypeId(Integer.parseInt(prototypeId));
        }

        List<Node> headRegions = root.selectNodes("/page/head/regions/region");
        List<Node> bodyRegions = root.selectNodes("/page/body/regions/region");
        List<Node> footRegions = root.selectNodes("/page/foot/regions/region");

        pageStructure.addHeadRegions(readRegions(headRegions));
        pageStructure.addBodyRegions(readRegions(bodyRegions));
        pageStructure.addFootRegions(readRegions(footRegions));

        return pageStructure;
    }

    public static String convertPageStructureToXml(PageStructure pageStructure) {
        Document doc = DocumentHelper.createDocument();
        Element pageElement = doc.addElement("page");
        pageElement.addAttribute("prototypeId", pageStructure.getPrototypeId() + "");
        pageElement.addAttribute("pageId", pageStructure.getPageId() + "");
        if (!pageStructure.getHeadRegions().isEmpty()) {
            Element headElement = pageElement.addElement("head");
            addRegionsElement(headElement, pageStructure.getHeadRegions());
        }
        if (!pageStructure.getBodyRegions().isEmpty()) {
            Element bodyElement = pageElement.addElement("body");
            addRegionsElement(bodyElement, pageStructure.getBodyRegions());
        }
        if (!pageStructure.getFootRegions().isEmpty()) {
            Element footElement = pageElement.addElement("foot");
            addRegionsElement(footElement, pageStructure.getFootRegions());
        }
        return doc.asXML();
    }

    private static void addRegionsElement(Element element, Map<String, Region> regions) {
        Element regionsElement = element.addElement("regions");
        for (Region region : regions.values()) {
            Element regionElement = regionsElement.addElement("region");
            regionElement.addAttribute("name", region.getName());
            addModulesElement(regionElement, region.getModules());
        }
    }

    private static void addModulesElement(Element element, List<Module> modules) {
        for (Module module : modules) {
            Element moduleElement = element.addElement("module");
            moduleElement.addAttribute("id", module.getModuleInstanceId() + "");
            moduleElement.addAttribute("prototypeId", module.getModulePrototype().getId() + "");
            moduleElement.addAttribute("name", module.getName());
            if (module.isTemplateModule()) {
                moduleElement.addAttribute("type", ModuleType.template.toString());
            } else {
                moduleElement.addAttribute("type", ModuleType.common.toString());
                CommonModule commonModule = (CommonModule) module.getModulePrototype();
                moduleElement.addAttribute("granularity", commonModule.getModuleGranularity().toString());
            }
            List<ModuleInstanceParam> params = module.getParams();
            if (params != null) {
                for (ModuleInstanceParam param : params) {
                    Element paramElement = moduleElement.addElement("param");
                    paramElement.addAttribute("name", param.getParamName());
                    paramElement.addText(param.getParamValue());
                }
            }
        }
    }

    /**
     * @param headRegions
     */
    private static Map<String, Region> readRegions(List<Node> headRegions) {
        Map<String, Region> regions = new HashMap<String, Region>();
        for (Node regionNode : headRegions) {
            String regionName = regionNode.valueOf("@name");
            Region region = new Region(regionName);
            List<Node> modules = regionNode.selectNodes("module");
            for (Node moduleNode : modules) {
                String id = moduleNode.valueOf("@id");
                int prototypeId = Integer.valueOf(moduleNode.valueOf("@prototypeId"));
                String name = moduleNode.valueOf("@name");
                String type = moduleNode.valueOf("@type");
                if (ModuleType.template.equals(type)) {
                    ModulePrototype prototype = new TemplateModule(prototypeId, name);
                    region.addModule(readModule(moduleNode, id, prototype));
                } else {
                    CommonModule prototype = new CommonModule(prototypeId, name);
                    String granularity = moduleNode.valueOf("@granularity");
                    if (StringUtils.isBlank(granularity)) {
                        prototype.setModuleGranularity(ModuleGranularity.DEFAULT);
                    } else {
                        prototype.setModuleGranularity(ModuleGranularity.valueOf(granularity));
                    }
                    region.addModule(readModule(moduleNode, id, prototype));
                }
            }
            regions.put(region.getName(), region);
        }
        return regions;
    }

    /**
     * @param moduleNode
     * @param id
     * @param prototype
     */
    private static Module readModule(Node moduleNode, String id, ModulePrototype prototype) {
        Module module = new Module(id, prototype);
        List<Node> params = moduleNode.selectNodes("param");
        List<ModuleInstanceParam> paramList = new ArrayList<ModuleInstanceParam>();
        for (Node param : params) {
            ModuleInstanceParam moduleInstanceParam = new ModuleInstanceParam(param.valueOf("@name"), param.getText());
            moduleInstanceParam.setModuleInstanceId(id);
            moduleInstanceParam.setModulePrototypeId(prototype.getId());
            paramList.add(moduleInstanceParam);
        }
        module.setParams(paramList);
        return module;
    }
}
