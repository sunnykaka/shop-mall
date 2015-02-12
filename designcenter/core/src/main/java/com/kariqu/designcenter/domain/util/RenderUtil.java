package com.kariqu.designcenter.domain.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kariqu.common.uri.URLBroker;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.domain.exception.ModuleConfigException;
import com.kariqu.designcenter.domain.model.*;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.ModulePrototype;
import com.kariqu.designcenter.domain.model.prototype.Parameter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 渲染相关的Util类
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-5-6 下午08:39:33
 */
public class RenderUtil {

    private static final String LINE = "\n";

    private static Log logger = LogFactory.getLog(RenderUtil.class);

    private static Pattern captureInTimeModuleId = Pattern.compile("<inTimeRender moduleId=\"(\\S+)\"/>");

    /**
     * 从文本中解析出待渲染的模块ID
     *
     * @param pageContent
     * @return
     */
    public static List<String> analyzeInTimeModuleIds(String pageContent) {
        List<String> moduleIdList = new ArrayList<String>();
        Matcher matcher = captureInTimeModuleId.matcher(pageContent);
        while (matcher.find()) {
            int count = matcher.groupCount();
            for (int i = 1; i <= count; i++)
                moduleIdList.add(matcher.group(i));
        }
        return moduleIdList;
    }

    /**
     * @param params
     * @return
     */
    public static List<ModuleInstanceParam> convert(List<Parameter> params) {
        if (params == null)
            return Collections.emptyList();
        List<ModuleInstanceParam> result = new LinkedList<ModuleInstanceParam>();
        for (Parameter param : params) {
            ModuleInstanceParam moduleInstanceParam = new ModuleInstanceParam(param.getName(), param.getValue()
                    .toString());
            result.add(moduleInstanceParam);
        }
        return result;
    }

    /**
     * 初始化body模块的toolbar
     *
     * @param module
     * @param urlBrokerFactory
     * @return
     */
    public static Toolbar initBodyToolbar(Module module, URLBrokerFactory urlBrokerFactory) {
        Toolbar toolbar = new Toolbar();
        URLBroker urlBroker = urlBrokerFactory.getUrl("moduleEdit");
        urlBroker.addQueryData("moduleInstanceId", module.getModuleInstanceId());
        urlBroker.addQueryData("prototypeId", module.getModulePrototype().getId());
        urlBroker.addQueryData("area", RenderConstants.RenderArea.body);
        toolbar.setEditUrl(urlBroker.toString());

        urlBroker = urlBrokerFactory.getUrl("moduleOperate");
        urlBroker.addQueryData("area", RenderConstants.RenderArea.body);
        toolbar.setDataUrl(urlBroker.toString());
        initModuleToolbar(module, toolbar);
        return toolbar;
    }

    /**
     * 初始化头部模块的toolbar
     *
     * @param module
     * @param urlBrokerFactory
     * @return
     */
    public static Toolbar initHeadToolbar(Module module, URLBrokerFactory urlBrokerFactory) {
        Toolbar toolbar = new Toolbar();
        URLBroker urlBroker = urlBrokerFactory.getUrl("moduleEdit");
        urlBroker.addQueryData("moduleInstanceId", module.getModuleInstanceId());
        urlBroker.addQueryData("prototypeId", module.getModulePrototype().getId());
        urlBroker.addQueryData("area", RenderConstants.RenderArea.head);
        toolbar.setEditUrl(urlBroker.toString());

        urlBroker = urlBrokerFactory.getUrl("moduleOperate");
        urlBroker.addQueryData("area", RenderConstants.RenderArea.head);
        toolbar.setDataUrl(urlBroker.toString());
        initModuleToolbar(module, toolbar);
        return toolbar;
    }


    /**
     * 初始化尾部模块的toolbar
     *
     * @param module
     * @param urlBrokerFactory
     * @return
     */
    public static Toolbar initFootToolbar(Module module, URLBrokerFactory urlBrokerFactory) {
        Toolbar toolbar = new Toolbar();
        URLBroker urlBroker = urlBrokerFactory.getUrl("moduleEdit");
        urlBroker.addQueryData("moduleInstanceId", module.getModuleInstanceId());
        urlBroker.addQueryData("prototypeId", module.getModulePrototype().getId());
        urlBroker.addQueryData("area", RenderConstants.RenderArea.foot);
        toolbar.setEditUrl(urlBroker.toString());

        urlBroker = urlBrokerFactory.getUrl("moduleOperate");
        urlBroker.addQueryData("area", RenderConstants.RenderArea.foot);
        toolbar.setDataUrl(urlBroker.toString());
        initModuleToolbar(module, toolbar);
        return toolbar;
    }

    /**
     * 构建产品模式页面模块中合并出来的 css
     *
     * @param pageStructure
     * @return
     */
    public static String buildPageCss(PageStructure pageStructure) {
        Set<String> cssSet = Sets.newHashSet();
        Map<Integer, Boolean> onceCss = Maps.newHashMap();
        List<Module> allCommonModule = pageStructure.getAllModules();
        for (Module module : allCommonModule) {
            CommonModule commonModule = (CommonModule) module.getModulePrototype();
            // 如果是自定义内容区则从模块参数中读取 css
            if (CommonModuleConstants.Custom_Html_Js_Module.equals(commonModule.getName()) ||
                    CommonModuleConstants.Custom_Global_Html_Js_Module.equals(commonModule.getName())) {
                List<ModuleInstanceParam> params = module.getParams();
                for (ModuleInstanceParam param : params) {
                    if (CommonModuleConstants.Module_Css_ParamName.equals(param.getParamName())
                            && StringUtils.isNotBlank(param.getParamValue())) {
                        for (String value : param.getParamValue().split(LINE)) {
                            if (StringUtils.isBlank(value) || value.trim().startsWith("//")) continue;

                            cssSet.add(value.trim());
                        }
                    }
                }
                continue;
            }

            // 非自定义内容区则模块自身有 css, 如果页面有多个相同的模块, 只加载一次就够了, 每个 css 一行一个在页面上进行渲染.
            String moduleCss = commonModule.getModuleCssContent();
            if (onceCss.get(commonModule.getId()) == null && StringUtils.isNotBlank(moduleCss)) {
                for (String css : moduleCss.split(LINE)) {
                    if (StringUtils.isBlank(css) || css.trim().startsWith("//")) continue;

                    cssSet.add(css.trim());
                    onceCss.put(commonModule.getId(), true);
                }
            }
        }

        StringBuilder cssbd = new StringBuilder();
        for (String css : cssSet) {
            cssbd.append(css).append(LINE);
        }
        return cssbd.toString();
    }

    /**
     * 构建产品模式页面模块中合并出来的js
     *
     * @param pageStructure
     * @return
     */
    public static String buildPageJavaScript(PageStructure pageStructure) {
        Set<String> jsSet = Sets.newHashSet();
        Map<Integer, Boolean> onceJs = Maps.newHashMap();
        List<Module> allCommonModule = pageStructure.getAllModules();
        for (Module module : allCommonModule) {
            CommonModule commonModule = (CommonModule) module.getModulePrototype();
            // 如果是自定义内容区则从模块参数中读取 js
            if (CommonModuleConstants.Custom_Html_Js_Module.equals(commonModule.getName()) ||
                    CommonModuleConstants.Custom_Global_Html_Js_Module.equals(commonModule.getName())) {
                List<ModuleInstanceParam> params = module.getParams();
                for (ModuleInstanceParam param : params) {
                    if (CommonModuleConstants.Module_Js_ParamName.equals(param.getParamName())
                            && StringUtils.isNotBlank(param.getParamValue())) {
                        jsSet.add(param.getParamValue().trim());
                    }
                }
                continue;
            }

            // 非自定义内容区则模块自身有 js, 如果页面有多个相同的模块, 只加载一次就够了
            String moduleJs = commonModule.getModuleJs();
            if (onceJs.get(commonModule.getId()) == null && StringUtils.isNotBlank(moduleJs)) {
                jsSet.add(moduleJs.replace("<script type=\"text/javascript\">", StringUtils.EMPTY).replace("</script>", StringUtils.EMPTY));
                onceJs.put(commonModule.getId(), true);
            }
        }

        StringBuilder jsbd = new StringBuilder();
        for (String js : jsSet) {
            jsbd.append(js).append(LINE);
        }
        return jsbd.toString();
    }

    private static void initModuleToolbar(Module module, Toolbar toolbar) {
        ModulePrototype modulePrototype = module.getModulePrototype();
        if (modulePrototype instanceof CommonModule) {
            CommonModule commonModule = (CommonModule) modulePrototype;
            toolbar.setModuleName(module.getName() + "(" + commonModule.getCaption() + ")");
        } else {
            toolbar.setModuleName(module.getName());
        }
        toolbar.setModuleId(module.getModuleInstanceId());
        toolbar.setPrototypeId(modulePrototype.getId());
        String isDelete = modulePrototype.getConfigValue(RenderConstants.IS_DELETE);
        toolbar.setDelete("true".equals(isDelete) || StringUtils.isBlank(isDelete));
        String isEdit = modulePrototype.getConfigValue(RenderConstants.IS_EDIT);
        toolbar.setEdit("true".equals(isEdit) || StringUtils.isBlank(isEdit));
    }

    public static CommonModule readModuleDir(File moduleDir, CommonModule commonModule) {
        CommonModule newModule = new CommonModule();
        BeanUtils.copyProperties(commonModule, newModule);
        try {
            File cssCodeFile = new File(moduleDir, commonModule.getName() + "Css.txt");
            File groovyCodeFile = new File(moduleDir, commonModule.getName() + ".groovy");
            File velocityCodeFile = new File(moduleDir, commonModule.getName() + ".vm");
            File editFormVelocityCodeFile = new File(moduleDir, commonModule.getName() + "Edit.vm");
            File moduleConfigFile = new File(moduleDir, commonModule.getName() + "Config.xml");
            File moduleJsFile = new File(moduleDir, commonModule.getName() + ".js");

            String cssCode = FileUtils.readFileToString(cssCodeFile, "UTF-8");
            String logicCode = FileUtils.readFileToString(groovyCodeFile, "UTF-8");
            String velocityCode = FileUtils.readFileToString(velocityCodeFile, "UTF-8");
            String editForm = FileUtils.readFileToString(editFormVelocityCodeFile, "UTF-8");
            String moduleConfig = FileUtils.readFileToString(moduleConfigFile, "UTF-8");
            String moduleJs = FileUtils.readFileToString(moduleJsFile, "UTF-8");

            //设置文件系统中的模块配置
            newModule.setModuleCssContent(cssCode);

            //设置文件系统中的groovy
            newModule.setLogicCode(logicCode);
            newModule.setEditLogicCode(logicCode);

            //设置文件系统中的编辑vm
            newModule.setFormContent(editForm);
            newModule.setEditFormContent(editForm);

            //设置文件系统中的模块vm
            newModule.setModuleContent(velocityCode);
            newModule.setEditModuleContent(velocityCode);

            //设置文件系统中的模块配置
            newModule.setModuleConfig(moduleConfig);

            //设置文件系统中的js
            newModule.setModuleJs(moduleJs);
            newModule.setEditModuleJs(moduleJs);

            //设置模块设置，比如缓存等
            newModule.setConfig(ModuleParamAndXmlConverter.readModuleConfig(moduleConfig));
        } catch (IOException e) {
            logger.error("开发者模式读取模块文件出错", e);
            String moduleContent = "开发者模式下从本地文件系统读取配置文件出错，具体错误信息如下：" + e.getMessage();
            commonModule.setModuleContent(moduleContent);
            commonModule.setEditModuleContent(moduleContent);
            return commonModule;
        } catch (ModuleConfigException e) {
            logger.error("开发者模式模块的配置文件有错", e);
            String moduleContent = "开发者模式下模块的配置文件有错：" + e.getMessage();
            commonModule.setModuleContent(moduleContent);
            commonModule.setEditModuleContent(moduleContent);
            return commonModule;
        }
        return newModule;
    }


}
