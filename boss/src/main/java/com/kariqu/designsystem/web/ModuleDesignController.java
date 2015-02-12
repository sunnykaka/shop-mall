package com.kariqu.designsystem.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.service.EditRenderPageService;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.service.CommonModuleContainer;
import com.kariqu.designcenter.service.ModuleInstanceParamService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * 模块装修，参数提交的控制器
 * User: Asion
 * Date: 12-5-16
 * Time: 上午11:28
 */
@Controller
public class ModuleDesignController {

    private final Log logger = LogFactory.getLog(ModuleDesignController.class);

    @Autowired
    private ModuleInstanceParamService moduleInstanceParamService;

    @Autowired
    private EditRenderPageService editRenderPageService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private CommonModuleContainer commonModuleContainer;

    /**
     * 客户端中的公共模块列表
     */
    @RequestMapping(value = "/design/moduleList/{area}/{regionName}")
    public String commonModuleClientList(Model model, @PathVariable("area") String area, @PathVariable("regionName") String regionName) {
        model.addAttribute("commonModuleList", commonModuleContainer.queryAllCommonModules());
        model.addAttribute("regionName", regionName);
        model.addAttribute("addUrl", urlBrokerFactory.getUrl("addModule").addQueryData("area", area).toString());
        return "moduleList";
    }

    /**
     * 模块添加后预览渲染
     *
     * @param moduleInstanceId
     * @param prototypeId
     * @return
     */
    @RequestMapping(value = "/design/module/preview/{moduleInstanceId}/{prototypeId}/{area}")
    public void modulePreview(HttpServletResponse response,
                              @PathVariable("moduleInstanceId") String moduleInstanceId,
                              @PathVariable("prototypeId") int prototypeId,
                              @PathVariable("area") String area) throws IOException {
        HashMap<String, Object> context = new HashMap<String, Object>();
        String content = editRenderPageService.renderCommonModuleWithToolbar(moduleInstanceId, prototypeId, context, RenderConstants.RenderArea.valueOf(area));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(content);
    }

    /**
     * 编辑表单的URL
     *
     * @param moduleInstanceId
     * @param modulePrototypeId
     * @return
     */
    @RequestMapping(value = "/design/shop/{area}/{moduleInstanceId}/{modulePrototypeId}")
    public String renderBodyForm(@PathVariable("area") RenderConstants.RenderArea area,
                                 @PathVariable("moduleInstanceId") String moduleInstanceId,
                                 @PathVariable("modulePrototypeId") int modulePrototypeId,
                                 HttpServletResponse response) throws IOException {
        List<ModuleInstanceParam> paramList = moduleInstanceParamService.queryModuleParamsByModuleInstanceId(moduleInstanceId);
        CommonModule commonModule = commonModuleContainer.getCommonModuleById(modulePrototypeId);
        Module module = new Module(moduleInstanceId, commonModule);
        module.setParams(paramList);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("moduleInstanceId", moduleInstanceId);
        context.put("modulePrototypeId", modulePrototypeId);
        context.put("area", area);
        String formHtml = editRenderPageService.renderCommonModuleEditForm(context, module);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(formHtml);
        return null;
    }


    /**
     * 提交模块的编辑参数
     *
     * @param request
     * @param moduleInstanceId
     * @param modulePrototypeId
     * @return
     */
    @Permission("提交装修参数")
    @RequestMapping(value = "/design/commitParam", method = RequestMethod.POST)
    public void commitParam(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam("area") RenderConstants.RenderArea area,
                            @RequestParam("moduleInstanceId") String moduleInstanceId,
                            @RequestParam("modulePrototypeId") Integer modulePrototypeId) throws IOException {
        try {
            Map<String, String> requestParamsMap = convertRequestMap(request.getParameterMap());
            moduleInstanceParamService.updateModuleInstanceParams(moduleInstanceId, requestParamsMap, modulePrototypeId);
        } catch (Exception e) {
            logger.error("装修模块提交时出现异常", e);
            new JsonResult(false, "装修发生错误，请检查参数是否匹配").toJson(response);
            return;
        }
        String previewUrl = urlBrokerFactory.getUrl("modulePreview").addQueryData("moduleInstanceId", moduleInstanceId).addQueryData("prototypeId", modulePrototypeId).addQueryData("area", area).toString();
        new JsonResult(true).addData("previewUrl", previewUrl).toJson(response);
    }

    private Map<String, String> convertRequestMap(Map parameterMap) {
        Map<String, String> result = new HashMap<String, String>();
        for (Object key : parameterMap.keySet()) {
            Object value = parameterMap.get(key);
            result.put(ObjectUtils.toString(key), (String) Array.get(value, 0));
        }
        return result;
    }

}
