package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import com.kariqu.categorycenter.domain.service.ProductCategoryService;
import com.kariqu.categorymanager.helper.CategoryCheckTreeJson;
import com.kariqu.categorymanager.helper.CategoryTreeJson;
import com.kariqu.categorymanager.helper.CheckedCategoryParentTreeJson;
import com.kariqu.common.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 三个类目树的渲染控制器
 * 1，后台类目
 * 2，前台类目
 * 3，带复选框的后台类目
 * User: Asion
 * Date: 11-7-8
 * Time: 下午2:12
 */

@Controller
public class CategoryTreeController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private NavigateCategoryService navigateCategoryService;

    /**
     * 后台类目树
     *
     * @return
     */
    @RequestMapping(value = "/category/tree")
    public
    @ResponseBody
    List<CategoryTreeJson> getCategoryTree(HttpServletRequest request) {
        List<ProductCategory> productCategories = productCategoryService.loadCategoryTree();
        List<CategoryTreeJson> nodeList = new LinkedList<CategoryTreeJson>();
        for (ProductCategory category : productCategories) {
            CategoryTreeJson rootNode = ExtNodeBuilder.buildCategoryTreeJsonNode(category);
            nodeList.add(rootNode);  //第一级类目
            ExtTreeBuilder.convertExtTreeNode(category.getChildren(), rootNode);
        }
        return nodeList;
    }

    /**
     * 判断该类目的父类目是否已关联
     *
     * @param navId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/navigate/category/checkParentHaveTree/{navId}")
    public void checkParentCategoryHaveAssociation(@PathVariable("navId") int navId, HttpServletResponse response) throws IOException {
        // 查询前台类目
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        // 查询父类目，此时父类目只有id有值。
        NavigateCategory parentNavigate = navigateCategory.getParent();

        // 不是第一级类目
        if (parentNavigate.getId() != -1) {
            // 查询父类目,此时有很多属性有值
            NavigateCategory parentNavigateCategory = navigateCategoryService.getNavigateCategory(parentNavigate.getId());
            // 父类目关联的后台类目
            List<Integer> productCategoryIds = parentNavigateCategory.getCategoryIds();
            if (productCategoryIds.size() == 0) {
                new JsonResult(false, "该父类目还没有关联").toJson(response);
            } else {
                new JsonResult(true).toJson(response);
            }
        } else {
            new JsonResult(true).toJson(response);
        }
    }

    /**
     * 生成前台类目关联后台类目的选择树
     * 节点可勾选，选择范围是类目的父亲的范围
     *
     * @return
     */
    @RequestMapping(value = "/navigate/category/checkTree/{navId}")
    public
    @ResponseBody
    List<CheckedCategoryParentTreeJson> getCategoryCheckTree(@PathVariable("navId") int navId) {
        // 查询前台类目
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        NavigateCategory parentCategory = null;
        if (navigateCategory.getParent().getId() != -1) {
            parentCategory = navigateCategoryService.getNavigateCategory(navigateCategory.getParent().getId());
        }
        List<Integer> categoryIdList = navigateCategory.getCategoryIds();
        // 关联的 后台类目有多个 且 父类目不是根 的时候, 使用其父类来查询
        boolean flag = false;
        if (parentCategory != null && parentCategory.getCategoryIds().size() > 1) {
            navigateCategory = parentCategory;
            flag = true;
        }

        List<CheckedCategoryParentTreeJson> nodeList = new LinkedList<CheckedCategoryParentTreeJson>();

        // 关联的后台类目ID
        List<Integer> categoryIds = navigateCategory.getCategoryIds();

        // 前台当前类目的父类目，此时父类目只有id有值。
        NavigateCategory parentNavigate = navigateCategory.getParent();

        buildNavCategoryTree(parentNavigate, categoryIds, flag, nodeList, categoryIdList);

        return nodeList;
    }

    /**
     * 构建当前的前台类目关联的后台类目树
     *
     * @param parentNavigate
     * @param categoryIds
     * @param nodeList
     */
    private void buildNavCategoryTree(NavigateCategory parentNavigate, List<Integer> categoryIds,
                                      boolean flag, List<CheckedCategoryParentTreeJson> nodeList, List<Integer> categoryIdList) {
        List<ProductCategory> productCategories;

        // 该类目为第一级类目
        if (parentNavigate.getId() == -1) {
            // 加载全部的后台类目
            productCategories = productCategoryService.loadCategoryTree();

            for (ProductCategory root : productCategories) {
                CategoryCheckTreeJson rootNode = ExtNodeBuilder.buildCategoryCheckTreeJsonNode(false, root, categoryIds);
                ExtTreeBuilder.convertExtTreeNode(root.getChildren(), rootNode, categoryIds);
                if (!(categoryIdList != null && categoryIdList.contains(root.getId()))) {
                    rootNode.setChecked(false);
                }

                if (categoryIds.contains(root.getId())) {
                    nodeList.add(rootNode);
                } else if (!flag) {
                    nodeList.add(rootNode);
                }
            }
        } else {
            // 查询父类目,此时有很多属性有值
            NavigateCategory parentNavigateCategory = navigateCategoryService.getNavigateCategory(parentNavigate.getId());
            // 父类目关联的后台类目ID
            List<Integer> parentAssociationList = parentNavigateCategory.getCategoryIds();

            for (Integer categoriesId : parentAssociationList) {
                // 加载关联后台类目的子类目
                productCategories = productCategoryService.loadCategoryTreeById(categoriesId);

                // 关联后台类目的父类目
                ProductCategory rootProductCategory = productCategoryService.getProductCategoryById(categoriesId);
                rootProductCategory.setChildren(productCategories);

                // 开始构造树    第一级节点
                CheckedCategoryParentTreeJson parentRootProduct = ExtNodeBuilder.buildCheckedCategoryParentTreeJsonNode(false, rootProductCategory);
                nodeList.add(parentRootProduct);

                // 第一级节点的子节点
                List<CategoryCheckTreeJson> categoryCheckTreeJsonList = new ArrayList<CategoryCheckTreeJson>();
                for (ProductCategory productCategory : productCategories) {
                    CategoryCheckTreeJson categoryCheckTreeJson = ExtNodeBuilder.buildCategoryCheckTreeJsonNode(false, productCategory, categoryIds);
                    categoryCheckTreeJsonList.add(categoryCheckTreeJson);
                }

                parentRootProduct.setChildren(categoryCheckTreeJsonList);
                List<CategoryCheckTreeJson> checkTreeJsonList = parentRootProduct.getChildren();

                List<ProductCategory> childCategory = rootProductCategory.getChildren(); // 第一级类目的子类目

                for (int i = 0; i < childCategory.size(); i++) {
                    ExtTreeBuilder.convertExtTreeNode(childCategory.get(i).getChildren(), checkTreeJsonList.get(i), categoryIds);
                }
            }
        }
    }

    /**
     * 生成前台类目关联后台类目的选择树 (只展示给用户看)
     * 叶子节点会出现checkbox
     *
     * @return
     */

    @RequestMapping(value = "/navigate/category/showCheckTree/{navId}")
    public
    @ResponseBody
    List<CategoryCheckTreeJson> getShowCategoryCheckTree(@PathVariable("navId") int navId) throws IOException {
        // 查询前台类目
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);

        // 关联的后台类目ID
        List<Integer> categoryIds = navigateCategory.getCategoryIds();

        List<CategoryCheckTreeJson> nodeList = new LinkedList<CategoryCheckTreeJson>();

        List<ProductCategory> productCategories = productCategoryService.loadCategoryTree();

        for (ProductCategory root : productCategories) {
            CategoryCheckTreeJson rootNode = ExtNodeBuilder.buildCategoryCheckTreeJsonNode(false, root, categoryIds);
            nodeList.add(rootNode);  //第一级类目
            ExtTreeBuilder.convertExtTreeNode(root.getChildren(), rootNode, categoryIds);
        }
        return nodeList;
    }

    /**
     * 生成前台导航类目树
     *
     * @return
     */
    @RequestMapping(value = "/navigate/category/tree")
    public
    @ResponseBody
    List<CategoryTreeJson> getNavCategoryTree() {
        List<NavigateCategory> navigateCategories = navigateCategoryService.loadNavCategoryTree();
        List<CategoryTreeJson> nodeList = new LinkedList<CategoryTreeJson>();
        for (NavigateCategory navigateCategory : navigateCategories) {
            CategoryTreeJson rootNode = ExtNodeBuilder.buildCategoryTreeJsonNode(navigateCategory);
            nodeList.add(rootNode);
            ExtTreeBuilder.convertExtNavTreeNode(navigateCategory.getChildren(), rootNode);
        }
        return nodeList;
    }

}
