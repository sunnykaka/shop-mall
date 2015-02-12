package com.kariqu.buyer.web.controller.shop;

import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.cmscenter.CmsService;
import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.Content;
import com.kariqu.cmscenter.domain.RenderTemplate;
import com.kariqu.cmscenter.domain.TemplateType;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.client.service.ProdRenderPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网站帮助内容区
 * User: Asion
 * Date: 13-6-21
 * Time: 上午11:53
 */
@Controller
public class CmsContentController {


    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private ProdRenderPageService prodRenderPageService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private RenderEngine renderEngine;

    @RequestMapping("/cms/{id}")
    @RenderHeaderFooter
    public String showContent(@PathVariable("id") int id, Model model, HttpServletResponse response) throws IOException {
        Content content = cmsService.queryContentById(id);
        if (content == null) {
            response.sendError(404);
            return null;
        }

        RenderTemplate renderTemplate = cmsService.queryRenderTemplateById(content.getTemplateId());
        if (renderTemplate == null) {
            response.sendError(404);
            return null;
        }

        Category category = cmsService.queryCategoryById(content.getCategoryId());
        if (category == null) {
            response.sendError(404);
            return null;
        }

        model.addAttribute("site_title", content.getTitle());
        model.addAttribute("body", cmsBody(content, renderTemplate, category));

        Map<String, Object> context = new HashMap<String, Object>();
        model.addAttribute("category", prodRenderPageService.prodRenderGlobalCommonModule("category", context).getPageContent());
        return "content/content";
    }

    private String cmsBody(Content content, RenderTemplate renderTemplate, Category category) {
        String body = "";
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("urlBroker", urlBrokerFactory);
        context.put("content", content);
        if (renderTemplate.getTemplateType() == TemplateType.Single) {
            body = renderEngine.render(renderTemplate.getTemplateContent(), context);
        } else if (renderTemplate.getTemplateType() == TemplateType.NavOne) {
            body = doNavOneRender(category, context, renderTemplate.getTemplateContent());
        } else if (category.getParent() != -1) {
            body = doNavTwoRender(category, context, renderTemplate.getTemplateContent());
        }
        return body;
    }

    private String doNavTwoRender(Category category, Map<String, Object> context, String templateContent) {
        List<Category> categories = cmsService.querySubCategory(category.getParent());
        context.put("subCategoryList", categories);
        context.put("rootCategory", cmsService.queryCategoryById(category.getParent()));
        Map<Integer, List<Content>> categoryContentMap = new HashMap<Integer, List<Content>>();
        for (Category child : categories) {
            List<Content> contents = cmsService.queryContentByCategoryId(child.getId());
            //有内容的类目才生成菜单
            if (contents.size() > 0) {
                categoryContentMap.put(child.getId(), contents);
            }
        }
        context.put("categoryContentMap", categoryContentMap);
        return renderEngine.render(templateContent, context);
    }

    private String doNavOneRender(Category category, Map<String, Object> context, String template) {
        List<Content> contents = cmsService.queryContentByCategoryId(category.getId());
        context.put("rootCategory", category);
        context.put("subCategoryList", contents);
        return renderEngine.render(template, context);
    }

}
