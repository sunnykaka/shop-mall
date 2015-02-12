package com.kariqu.cmssystem.web;

import com.kariqu.cmscenter.CmsService;
import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.Content;
import com.kariqu.cmscenter.domain.RenderTemplate;
import com.kariqu.cmssystem.helper.ContentVo;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-10-9
 * Time: 下午1:47
 */
@Controller
public class ContentController {

    private final Log logger = LogFactory.getLog(ContentController.class);

    @Autowired
    private CmsService cmsService;

    /**
     * 根据栏目类别获取内容
     *
     * @param id
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/cms/content/contentList/{id}")
    public void contentGrid(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        List<Content> contents = cmsService.queryContentByCategoryId(id);
        List<ContentVo> contentVos = new ArrayList<ContentVo>();
        for (Content content : contents) {
            ContentVo contentVo = new ContentVo();
            contentVo.setContent(content.getContent());
            contentVo.setId(content.getId());
            contentVo.setTitle(content.getTitle());
            contentVo.setUrl(content.getUrl());
            contentVo.setPriority(content.getPriority());
            contentVo.setTemplateId(content.getTemplateId());
            if (content.getTemplateId() > 0) {
                RenderTemplate renderTemplate = cmsService.queryRenderTemplateById(content.getTemplateId());
                contentVo.setTemplateName(renderTemplate.getName());
            }
            Category category = cmsService.queryCategoryById(content.getCategoryId());
            contentVo.setCategoryName(category.getName());
            contentVos.add(contentVo);
        }
        new JsonResult(true).addData("totalCount", contentVos.size()).addData("result", contentVos).toJson(response);
    }

    /**
     * 更新一个栏目子类目的内容优先级
     *
     * @param content
     */
    @RequestMapping(value = "/cms/content/priority/update", method = RequestMethod.POST)
    public void updateNavCategoryPriority(Content content, HttpServletResponse response) throws IOException {
        try {
            Content currentContent = cmsService.queryContentById(content.getId());
            currentContent.setPriority(content.getPriority());
            cmsService.updateContent(currentContent);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("更新一个栏目类别的内容优先级错误：" + e);
            new JsonResult(false, "更新失败，请确保你输入合法").toJson(response);
        }

    }

    @RequestMapping(value = "/cms/content/add", method = RequestMethod.POST)
    public void createContent(Content content, HttpServletResponse response) throws IOException {
        try {
            cmsService.createContent(content);
        } catch (Exception e) {
            logger.error("添加栏目内容异常：" + e);
            new JsonResult(false, "添加栏目内容出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    @Permission("修改CMS内容")
    @RequestMapping(value = "/cms/content/update", method = RequestMethod.POST)
    public void updateContent(Content content, HttpServletResponse response) throws IOException {
        try {
            Content currentContent = cmsService.queryContentById(content.getId());
            currentContent.setContent(content.getContent());
            currentContent.setTitle(content.getTitle());
            currentContent.setTemplateId(content.getTemplateId());
            cmsService.updateContent(currentContent);
        } catch (Exception e) {
            logger.error("编辑栏目内容异常" + e);
            new JsonResult(false, "编辑更新栏目内容出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @Permission("删除CMS内容")
    @RequestMapping(value = "/cms/content/delete", method = RequestMethod.POST)
    public void deleteBatchContent(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids)
                cmsService.deleteContentById(id);
        } catch (Exception e) {
            logger.error("批量删除栏目内容异常：" + e);
            new JsonResult(false, "批量删除除栏目内容出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

}
