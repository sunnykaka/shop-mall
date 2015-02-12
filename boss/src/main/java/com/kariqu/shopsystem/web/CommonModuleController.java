package com.kariqu.shopsystem.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.designcenter.client.service.ModulePreviewService;
import com.kariqu.designcenter.domain.model.CommonModuleConstants;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.service.CommonModuleService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * 公共模块的web控制器
 * User: Asion
 * Date: 11-8-3
 * Time: 上午9:43
 */

@Controller
public class CommonModuleController {

    private final Log logger = LogFactory.getLog(CommonModuleController.class);

    @Autowired
    private CommonModuleService commonModuleService;

    @Autowired
    private ModulePreviewService modulePreviewService;

    /**
     * 预览某个模块
     *
     * @param name
     * @param model
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/page/module/common/preview/{name}")
    public String previewCommonModule(@PathVariable("name") String name, Model model) throws IOException {
        model.addAttribute("pageContent", modulePreviewService.previewCommonModule(name, new HashMap<String, Object>()));
        return "layout/moduleLayout";
    }

    /**
     * 发布公共模块，即拷贝编辑字段到产品模式
     *
     * @param ids
     * @param response
     * @throws java.io.IOException
     */
    @Permission("发布公共模块")
    @RequestMapping(value = "/page/module/common/release", method = RequestMethod.POST)
    public void releaseCommonModule(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids)
                commonModuleService.releaseCommonModule(id);
        } catch (Exception e) {
            logger.error("模板系统的发布系统模板异常：" + e);
            new JsonResult(false, "发布系统模板失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/page/module/common/list")
    public void commonModuleGrid(HttpServletResponse response) throws IOException {
        List<CommonModule> commonModules = commonModuleService.queryAllCommonModule();
        new JsonResult(true).addData("totalCount", commonModules.size()).addData("result", commonModules).toJson(response);
    }

    @RequestMapping(value = "/page/module/special")
    public void specialModuleGrid(HttpServletResponse response) throws IOException {
        CommonModule specialModule = commonModuleService.getCommonModuleForRendering(CommonModuleConstants.Custom_Html_Js_Module, "");
        new JsonResult(true).addData("result", specialModule).toJson(response);
    }


    @Permission("创建公共模块")
    @RequestMapping(value = "/page/module/common/new", method = RequestMethod.POST)
    public void createCommonModule(CommonModule commonModule, HttpServletResponse response) throws IOException {
        try {
            if (commonModuleService.existCommonModule(commonModule.getName())) {
                new JsonResult(false, "模块重名了").toJson(response);
                return;
            }
            commonModule.setVersion("1.0");
            commonModuleService.createCommonModule(commonModule);
        } catch (Exception e) {
            logger.error("模板系统的增加系统模块异常：" + e);
            new JsonResult(false, "增加系统模块出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/page/module/common/delete", method = RequestMethod.POST)
    @Permission("删除公共模块")
    public void deleteCommonModule(@RequestParam("id") int id, HttpServletResponse response) throws IOException {
        try {
            commonModuleService.deleteCommonModule(id);
        } catch (Exception e) {
            logger.error("模板系统的删除系统模块异常：" + e);
            new JsonResult(false, "删除系统模块出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @Permission("删除公共模块")
    @RequestMapping(value = "/page/module/common/delete/batch", method = RequestMethod.POST)
    public void deleteBatchCommonModule(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids)
                commonModuleService.deleteCommonModule(id);
        } catch (Exception e) {
            logger.error("模板系统的批量删除系统模块异常：" + e);
            new JsonResult(false, "批量删除系统模块出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/page/module/common/update", method = RequestMethod.POST)
    @Permission("修改公共模块")
    public void updateCommonModule(CommonModule commonModule, HttpServletResponse response) throws IOException {
        try {
            CommonModule currentCommonModule = commonModuleService.getCommonModuleById(commonModule.getId());
            currentCommonModule.updateFrom(commonModule);
            commonModuleService.updateCommonModule(currentCommonModule);
        } catch (Exception e) {
            logger.error("模板系统的编辑系统模块异常" + e);
            new JsonResult(false, "编辑系统模块出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

}
