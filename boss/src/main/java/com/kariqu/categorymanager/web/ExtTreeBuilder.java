package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorymanager.helper.CategoryCheckTreeJson;
import com.kariqu.categorymanager.helper.CategoryTreeJson;

import java.util.List;

/**
 * Ext 树面板的类目节点递归构建
 * User: Asion
 * Date: 11-10-8
 * Time: 下午2:23
 */
public class ExtTreeBuilder {

    /**
     * 构建后台类目节点
     * @param productCategories
     * @param rootNode
     */
    static void convertExtTreeNode(List<ProductCategory> productCategories, CategoryTreeJson rootNode) {
        if (productCategories == null || productCategories.isEmpty()) {
            return;
        }
        for (ProductCategory productCategory : productCategories) {
            CategoryTreeJson childNode = rootNode.addNode(ExtNodeBuilder.buildCategoryTreeJsonNode(productCategory));
            convertExtTreeNode(productCategory.getChildren(), childNode);
        }
    }

    /**
     * 构建后台类目节点，但是带选择框
     * @param productCategories
     * @param rootNode
     * @param categoryIds
     */
    static void convertExtTreeNode(List<ProductCategory> productCategories, CategoryCheckTreeJson rootNode, List<Integer> categoryIds) {
        if (productCategories == null || productCategories.isEmpty()) {
            return;
        }
        for (ProductCategory productCategory : productCategories) {
            //如果节点被选了或者节点无效则儿子无效
            boolean childDisable = rootNode.isChecked() || rootNode.isDisabled();
            CategoryCheckTreeJson childNode = rootNode.addNode(ExtNodeBuilder.buildCategoryCheckTreeJsonNode(childDisable,productCategory, categoryIds));
            convertExtTreeNode(productCategory.getChildren(), childNode, categoryIds);
        }
    }

    /**
     * 构建前台类目节点
     * @param navigateCategories
     * @param rootNode
     */
    static void convertExtNavTreeNode(List<NavigateCategory> navigateCategories, CategoryTreeJson rootNode) {
        if (navigateCategories == null || navigateCategories.isEmpty()) {
            return;
        }
        for (NavigateCategory navigateCategory : navigateCategories) {
            CategoryTreeJson childNode = rootNode.addNode(ExtNodeBuilder.buildCategoryTreeJsonNode(navigateCategory));
            convertExtNavTreeNode(navigateCategory.getChildren(), childNode);
        }
    }
}
