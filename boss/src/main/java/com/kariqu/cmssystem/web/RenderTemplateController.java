package com.kariqu.cmssystem.web;

import com.kariqu.cmscenter.CmsService;
import com.kariqu.cmscenter.domain.RenderTemplate;
import com.kariqu.cmscenter.domain.TemplateType;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-10-10
 * Time: 下午3:35
 */

@Controller
public class RenderTemplateController {

    private final Log logger = LogFactory.getLog(RenderTemplateController.class);

    @Autowired
    private CmsService cmsService;


    /**
     * 获取模板列表
     *
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/cms/template/list")
    public void renderTemplateGrid(HttpServletResponse response) throws IOException {
        List<RenderTemplate> renderTemplates = cmsService.queryAllRenderTemplate();
        new JsonResult(true).addData("totalCount", renderTemplates.size()).addData("result", renderTemplates).toJson(response);
    }

    /**
     * 添加模板
     *
     * @param renderTemplate
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/cms/template/add", method = RequestMethod.POST)
    public void createRenderTemplate(RenderTemplate renderTemplate, HttpServletResponse response) throws IOException {
        try {
            cmsService.createRenderTemplate(renderTemplate);
        } catch (Exception e) {
            logger.error("添加模板异常：" + e);
            new JsonResult(false, "添加模板出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 修改模板
     *
     * @param renderTemplate
     * @param response
     * @throws IOException
     */
    @Permission("修改内容模板")
    @RequestMapping(value = "/cms/template/update", method = RequestMethod.POST)
    public void updateRenderTemplate(RenderTemplate renderTemplate, HttpServletResponse response) throws IOException {
        try {
            RenderTemplate dbRenderTemplate = cmsService.queryRenderTemplateById(renderTemplate.getId());
            dbRenderTemplate.setName(renderTemplate.getName());
            dbRenderTemplate.setTemplateContent(renderTemplate.getTemplateContent());
            dbRenderTemplate.setTemplateType(renderTemplate.getTemplateType());
            cmsService.updateRenderTemplate(dbRenderTemplate);
        } catch (Exception e) {
            logger.error("编辑模板异常" + e);
            new JsonResult(false, "编辑模板出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 批量删除模板
     *
     * @param ids
     * @param response
     * @throws IOException
     */
    @Permission("删除内容模板")
    @RequestMapping(value = "/cms/template/delete", method = RequestMethod.POST)
    public void deleteBatchRenderTemplate(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids)
                cmsService.deleteRenderTemplateById(id);
        } catch (Exception e) {
            logger.error("批量删除模板异常：" + e);
            new JsonResult(false, "批量删除模板出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 获取不属于首页模板
     *
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/cms/template/templateNotIndexList")
    public void templateNotIndexList(HttpServletResponse response) throws IOException {
        List<RenderTemplate> attributeValue = cmsService.queryAllRenderTemplate();
        //过滤出首页类型的模板
        List<RenderTemplate> indexList = new ArrayList<RenderTemplate>();
        for (RenderTemplate renderTemplate : attributeValue) {
            if (renderTemplate.getTemplateType() != TemplateType.Index) {
                indexList.add(renderTemplate);
            }
        }
        new JsonResult(true).addData("templateList", indexList).toJson(response);
    }
}
