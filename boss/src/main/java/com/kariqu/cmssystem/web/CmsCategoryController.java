package com.kariqu.cmssystem.web;

import com.kariqu.categorymanager.helper.CategoryTreeJson;
import com.kariqu.cmscenter.CmsService;
import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.Content;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: wendy
 * @since 1.0.0
 *        Date: 12-10-8
 *        Time: 下午1:17
 */
@Controller
public class CmsCategoryController {
    private final Log logger = LogFactory.getLog(CmsCategoryController.class);

    @Autowired
    private CmsService cmsService;

    /**
     * 栏目类别树状列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/cms/category/list")
    public
    @ResponseBody
    List<CategoryTreeJson> getCategoryTree(HttpServletRequest request) {
        List<Category> categories = cmsService.querySubCategory(-1);
        List<CategoryTreeJson> nodeList = new LinkedList<CategoryTreeJson>();
        for (Category category : categories) {
            CategoryTreeJson categoryTreeJson = new CategoryTreeJson();
            List<Category> subCategories = cmsService.querySubCategory(category.getId());
            if (subCategories.size() > 0) {
                categoryTreeJson.setLeaf(false);
                for (Category subCategory : subCategories) {
                    CategoryTreeJson treeJson = new CategoryTreeJson();
                    treeJson.setId(subCategory.getId());
                    treeJson.setText(subCategory.getName());
                    treeJson.setLeaf(true);
                    categoryTreeJson.addNode(treeJson);
                }
            } else {
                categoryTreeJson.setLeaf(true);
            }
            categoryTreeJson.setText(category.getName());
            categoryTreeJson.setId(category.getId());
            nodeList.add(categoryTreeJson);
        }
        return nodeList;
    }

    /**
     * 获取栏目父节点类别
     *
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/cms/category/comboBoxList")
    public void categoriesList(HttpServletResponse response) throws IOException {
        List<Category> categories = cmsService.querySubCategory(-1);
        new JsonResult(true).addData("categoryList", categories).toJson(response);
    }

    /**
     * 加载某个栏目的所有子类目
     *
     * @return
     */
    @RequestMapping(value = "/cms/category/list/{parentId}")
    public void querySubCategoriesByNavId(@PathVariable("parentId") int parentId, HttpServletResponse response) throws IOException {
        List<Category> categoryList = cmsService.querySubCategory(parentId);
        new JsonResult(true).addData("totalCount", categoryList.size()).addData("result", categoryList).toJson(response);
    }

    /**
     * 更新一个栏目子类目的优先级
     *
     * @param category
     */
    @RequestMapping(value = "/cms/category/priority/update", method = RequestMethod.POST)
    public void updateNavCategoryPriority(Category category, HttpServletResponse response) throws IOException {
        try {
            Category currentCategory = cmsService.queryCategoryById(category.getId());
            currentCategory.setPriority(category.getPriority());
            cmsService.updateCategory(currentCategory);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("更新一个栏目类别的优先级错误：" + e);
            new JsonResult(false, "更新失败，请确保你输入合法").toJson(response);
        }

    }

    /**
     * 添加栏目类别
     *
     * @param category
     * @param response
     * @throws IOException
     */
    @Permission("添加内容分类")
    @RequestMapping(value = "/cms/category/add", method = RequestMethod.POST)
    public void createCategory(Category category, HttpServletResponse response) throws IOException {
        try {
            checkDirectory(category);
            Category currentCategory = cmsService.queryCategoryByName(category.getName());
            if (currentCategory != null) {
                new JsonResult(false, "栏目名称已经存在").toJson(response);
                return;
            }
            cmsService.createCategory(category);
            new JsonResult(true).toJson(response);

        } catch (Exception e) {
            logger.error("栏目添加错误：" + e);
            new JsonResult(false, "栏目添加失败").toJson(response);
        }

    }

    /**
     * 删除栏目类别
     *
     * @param id
     * @param request
     * @param response
     * @throws IOException
     */
    @Permission("删除内容分类")
    @RequestMapping(value = "/cms/category/delete", method = RequestMethod.POST)
    public void deleteCategory(int id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            cmsService.deleteCategoryById(id);    //删除栏目类别
            List<Content> contents = cmsService.queryContentByCategoryId(id);
            //删除当前栏目类别的内容
            if (contents.size() > 0) {
                for (Content content : contents) {
                    cmsService.deleteContentById(content.getId());
                }
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除栏目类别错误：" + e);
            new JsonResult(false, "删除失败").toJson(response);
        }

    }


    /**
     * 修改目类别
     *
     * @param category
     * @param response
     * @param request
     */
    @Permission("修改内容分类")
    @RequestMapping(value = "/cms/category/update", method = RequestMethod.POST)
    public void updateCategory(Category category, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            checkDirectory(category);
            Category currentCategory = cmsService.queryCategoryById(category.getId());
            if (currentCategory.getParent() != -1) {
                currentCategory.setParent(category.getParent());
            }
            currentCategory.setDirectory(category.getDirectory());
            currentCategory.setName(category.getName());

            cmsService.updateCategory(category);
        } catch (Exception e) {
            logger.error("商品管理的修改商品属性值或者类目值异常：" + e);
            new JsonResult(false, "修改商品的属性值或者类目值出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 加载栏目分类单个对象的json格式
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/cms/category/category/{id}")
    public void loadCategory(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        Category category = cmsService.queryCategoryById(id);
        new JsonResult(true).addData("object", category).toJson(response);
    }

    /**
     * 检查栏目是否设置目录
     *
     * @param category
     */
    private void checkDirectory(Category category) {
        if (StringUtils.isEmpty(category.getDirectory())) {
            if (category.getParent() == -1) {
                category.setDirectory("help");
            } else {
                Category parent = cmsService.queryCategoryById(category.getParent());
                category.setDirectory(parent.getDirectory());
            }
        }
    }
}
