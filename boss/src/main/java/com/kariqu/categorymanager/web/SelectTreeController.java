package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import com.kariqu.categorycenter.domain.service.ProductCategoryService;
import com.kariqu.common.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 这个控制器用于输出树的展开路径
 * User: Asion
 * Date: 12-7-13
 * Time: 下午12:47
 */
@Controller
public class SelectTreeController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private NavigateCategoryService navigateCategoryService;


    /**
     * 构造后台类目某个节点的选择路径
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/category/selectPath/{categoryId}")
    public
    @ResponseBody
    String getCategoryPath(@PathVariable("categoryId") int categoryId) {
        if (categoryId == -1) {
            return "/-1";
        }
        List<ProductCategory> parentCategories = productCategoryService.getParentCategories(categoryId, true);
        StringBuilder sb = new StringBuilder("/-1");
        for (ProductCategory parentCategory : parentCategories) {
            sb.append("/");
            sb.append(parentCategory.getId());
        }
        sb.append("/" + categoryId);
        return sb.toString();
    }

    /**
     * 构造后台类目某个节点的选择路径 （删除栏目时用）
     * 后面加的斜杠"/" 是为了让当前删除子节点的父节点能展开
     *
     * @param parentCategoryId
     * @return
     */
    @RequestMapping(value = "/category/selectPathAsDelete/{parentCategoryId}")
    public void getCategoryPathAsDelete(@PathVariable("parentCategoryId") int parentCategoryId, HttpServletResponse response) throws IOException {
        new JsonResult(true, this.getCategoryPath(parentCategoryId) + "/").toJson(response);
    }


    /**
     * 构造前台类目关联后台类目时已被选择的节点的展开路径
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/navigate/category/expandPaths/{navId}")
    public void getSelectLeafIdPath(@PathVariable("navId") int navId, HttpServletResponse response) throws IOException {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        List<String> paths = new LinkedList<String>();
        for (int leafId : navigateCategory.getCategoryIds()) {
            List<ProductCategory> parentCategories = productCategoryService.getParentCategories(leafId, true);
            StringBuilder sb = new StringBuilder("/-1");
            for (ProductCategory parentCategory : parentCategories) {
                sb.append("/");
                sb.append(parentCategory.getId());
            }
            if (!paths.contains(sb.toString()))
                paths.add(sb.toString());
        }
        new JsonResult(true).addData("path",paths).toJson(response);
    }

    /**
     * 构造前台类目某个节点的选择路径
     *
     * @param navigateCategoryId
     * @return
     */
    @RequestMapping(value = "/navigate/category/selectPath/{navigateCategoryId}")
    public
    @ResponseBody
    String getNavCategoryPath(@PathVariable("navigateCategoryId") int navigateCategoryId) {
        if (navigateCategoryId == -1) {
            return "/-1";
        }
        List<NavigateCategory> navigateCategory = navigateCategoryService.getParentCategories(navigateCategoryId, true);
        StringBuilder sb = new StringBuilder("/-1");
        for (NavigateCategory parentNavigateCategory : navigateCategory) {
            sb.append("/");
            sb.append(parentNavigateCategory.getId());
        }
        sb.append("/" + navigateCategoryId);
        return sb.toString();
    }

    /**
     * 构造前台类目某个节点的选择路径（删除栏目时用）
     * 后面加的斜杠"/" 是为了让当前删除子节点的父节点能展开
     *
     * @param navigateParentCategoryId
     * @return
     */
    @RequestMapping(value = "/navigate/category/selectPathAsDelete/{navigateParentCategoryId}")
    public
    @ResponseBody
    String getNavCategoryPathAsDelete(@PathVariable("navigateParentCategoryId") int navigateParentCategoryId) {
        return this.getNavCategoryPath(navigateParentCategoryId) + "/";
    }


}
