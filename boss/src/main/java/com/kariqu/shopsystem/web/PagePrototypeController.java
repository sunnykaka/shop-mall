package com.kariqu.shopsystem.web;

import com.kariqu.common.JsonResult;
import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.client.service.DebugRenderPageService;
import com.kariqu.designcenter.domain.model.prototype.PagePrototype;
import com.kariqu.designcenter.service.PagePrototypeService;
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
import java.util.Map;


/**
 * 页面原型控制器
 * User: Asion
 * Date: 12-4-20
 * Time: 上午1:12
 */
@Controller
public class PagePrototypeController {

    private final Log logger = LogFactory.getLog(PagePrototypeController.class);

    @Autowired
    private PagePrototypeService pagePrototypeService;

    @Autowired
    private DebugRenderPageService debugRenderPageService;


    /**
     * debug原型页
     *
     * @param pageId
     * @param model
     * @return
     */
    @RequestMapping(value = "/page/prototype/debug/{pageId}")
    public String debug(@PathVariable("pageId") int pageId, Model model) {
        Map<String, Object> context = new HashMap<String, Object>();
        RenderResult renderResult = debugRenderPageService.debugPagePrototype(pageId, context);
        model.addAttribute("pageContent", renderResult.getPageContent());
        return "layout/prototypeLayout";
    }

    /**
     * 原型列表
     *
     * @return
     */
    @RequestMapping(value = "/page/prototype/list")
    public void pageList(HttpServletResponse response) throws IOException {
        List<PagePrototype> pagePrototypeList = pagePrototypeService.queryAllPagePrototype();
        new JsonResult(true).addData("totalCount", pagePrototypeList.size()).addData("result", pagePrototypeList).toJson(response);
    }

    /**
     * 添加页面原型
     */
    @RequestMapping(value = "/page/prototype/new", method = RequestMethod.POST)
    public void pagePrototypeAdd(PagePrototype pagePrototype, HttpServletResponse response) throws IOException {
        try {
            pagePrototypeService.createPagePrototype(pagePrototype);
        } catch (Exception e) {
            logger.error("模板系统的添加页面原型异常：" + e);
            new JsonResult(false, "添加页面原型失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 发布页面原型
     *
     * @param id
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/page/prototype/release", method = RequestMethod.POST)
    public void releasePagePrototype(int id, HttpServletResponse response) throws IOException {
        try {
            pagePrototypeService.releasePagePrototypeId(id);
        } catch (Exception e) {
            logger.error("模板系统的发布页面原型异常：" + e);
            new JsonResult(false, "发布页面原型失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 发布页面原型
     *
     * @param ids
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/page/prototype/batch/release", method = RequestMethod.POST)
    public void releaseBatchPagePrototype(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int pagePrototypeId : ids) {
                pagePrototypeService.releasePagePrototypeId(pagePrototypeId);
            }
        } catch (Exception e) {
            logger.error("模板系统的发布页面原型异常：" + e);
            new JsonResult(false, "发布页面原型失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 更新页面原型
     *
     * @param pagePrototype
     */
    @RequestMapping(value = "/page/prototype/update", method = RequestMethod.POST)
    public void updatePagePrototype(PagePrototype pagePrototype, HttpServletResponse response) throws IOException {
        try {
            PagePrototype currentPagePrototype = pagePrototypeService.getPagePrototypeById(pagePrototype.getId());
            currentPagePrototype.setPageCode(pagePrototype.getPageCode());
            currentPagePrototype.setAreaType(pagePrototype.getAreaType());
            currentPagePrototype.setDescription(pagePrototype.getDescription());
            currentPagePrototype.setName(pagePrototype.getName());
            pagePrototypeService.updatePagePrototype(pagePrototype);
        } catch (Exception e) {
            logger.error("模板系统的更新页面原型异常：" + e);
            new JsonResult(false, "更新页面原型失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);

    }

    /**
     * 删除原型页面
     *
     * @param id
     */
    @RequestMapping(value = "/page/prototype/delete", method = RequestMethod.POST)
    public void deletePagePrototype(int id, HttpServletResponse response) throws IOException {
        try {
            pagePrototypeService.deletePagePrototype(id);
        } catch (Exception e) {
            logger.error("模板系统的删除页面原型异常：" + e);
            new JsonResult(false, "删除页面原型失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 批量删除原型页面
     *
     * @param ids
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/page/prototype/delete/batch", method = RequestMethod.POST)
    public void deletePagePrototype(@RequestParam int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                pagePrototypeService.deletePagePrototype(id);
            }
        } catch (Exception e) {
            logger.error("模板系统的批量删除页面原型异常：" + e);
            new JsonResult(false, "批量删除页面原型失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

}
